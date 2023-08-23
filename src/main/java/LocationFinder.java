import data.Data;
import listeners.CommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class LocationFinder {

    public static void main(String[] args) {
        JDABuilder builder = JDABuilder.createDefault(Data.DISCORD_TOKEN.getKey());
        builder.setActivity(Activity.listening("you"));
        builder.enableIntents(GatewayIntent.DIRECT_MESSAGES);
        builder.addEventListeners(new CommandListener());
        JDA jda = builder.build();

        jda.updateCommands().addCommands(
                Commands.slash("findlocation", "Find the location of an IP")
                        .addOption(OptionType.STRING, "ip", "The IP Address") // optional reason
        ).queue();
    }
}

