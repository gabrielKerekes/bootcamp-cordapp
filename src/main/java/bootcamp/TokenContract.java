package bootcamp;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.transactions.LedgerTransaction;
import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;    
import static net.corda.core.contracts.ContractsDSL.requireThat;

import java.util.List;

/* Our contract, governing how our state will evolve over time.
 * See src/main/java/examples/ArtContract.java for an example. */
public class TokenContract implements Contract {
    public static String ID = "bootcamp.TokenContract";

    @Override
    public void verify(LedgerTransaction tx) throws IllegalArgumentException {
        List<ContractState> inputs = tx.getInputStates();
        List<ContractState> outputs = tx.getOutputStates();

        List<CommandWithParties<CommandData>> commands = tx.getCommands();
        if (commands.size() != 1) throw new IllegalArgumentException("tx should have only one command");
        if (!(commands.get(0).getValue() instanceof TokenContract.Commands.Issue)) throw new IllegalArgumentException("");

        CommandData command = commands.get(0).getValue();

        if (inputs.size() != 0) throw new IllegalArgumentException("must have zero inputs");
        if (outputs.size() != 1) throw new IllegalArgumentException("must have one output");
        if (!(outputs.get(0) instanceof TokenState)) throw new IllegalArgumentException("outputs must be of type TokenState");

        TokenState tokenState = (TokenState) outputs.get(0);

        if (!(tokenState.getAmount() > 0)) throw new IllegalArgumentException("Token amount must be greater than 0");
        if (!commands.get(0).getSigners().contains(tokenState.getIssuer().getOwningKey()))
            throw new IllegalArgumentException("Issuer must be required signer");
    }


    public interface Commands extends CommandData {
        class Issue implements Commands { }
    }
}
