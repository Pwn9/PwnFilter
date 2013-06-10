package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Fine the user by extracting money from his economy account.
 */
@SuppressWarnings("UnusedDeclaration")
public class Actionfine implements Action {

    String messageString; // Message to apply to this action
    double fineAmount; // How much to fine the player.

    public void init(String s)
    {
        String[] parts = s.split("\\s",2);

        try {
            fineAmount = Double.parseDouble(parts[0]);
        } catch (NumberFormatException e ) {
            fineAmount = 0.00;
        }

        String message = (parts.length > 1)?parts[1]:"";
        messageString = PwnFilter.prepareMessage(parts[1],"finemsg");
        if (PwnFilter.economy == null) {
            PwnFilter.logger.warning("Parsed rule requiring an Economy, but one was not detected. " +
                    "Check Vault configuration, or remove 'then fine' rules.");
        }

    }

    public boolean execute(final FilterState state ) {
        if (PwnFilter.economy != null ) {
            EconomyResponse resp = PwnFilter.economy.withdrawPlayer(state.playerName,fineAmount);
            if (resp.transactionSuccess()) {
                state.addLogMessage(String.format("Fined %s : %f",state.playerName,resp.amount));
            } else {
                state.addLogMessage(String.format("Failed to fine %s : %f. Error: %s",
                        state.playerName,resp.amount,resp.errorMessage));
                return false;
            }
            Bukkit.getScheduler().runTask(state.plugin, new BukkitRunnable() {
                @Override
                public void run() {
                    state.player.sendMessage(messageString);
                }
            });

            return true;

        } else return false;
    }
}
