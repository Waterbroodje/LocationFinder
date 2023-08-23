package listeners;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import data.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equalsIgnoreCase("findlocation")) return;

        String ip = event.getOption("ip").getAsString();

        try {
            URL apiUrl = new URL("https://apis.thatapicompany.com/geo-ip-api-community/locations/iplookup?ip=" + ip);
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestProperty("X-BLOBR-KEY", Data.API_KEY.getKey());

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();

                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setTitle("Location Information")
                        .setDescription("Here's the information for IP Address " + ip)
                        .setColor(new Color(42, 45, 48, 255))
                        .addField("Country", jsonObject.getAsJsonObject("country").get("name").getAsString(), true)
                        .addField("Region", jsonObject.getAsJsonObject("locationInfo").get("region").getAsString(), true)
                        .addField("City", jsonObject.getAsJsonObject("locationInfo").get("city").getAsString(), true)
                        .addField("Dialing Code", jsonObject.getAsJsonObject("country").get("dialing_code").getAsString(), true)
                        .addField("Currency", jsonObject.getAsJsonObject("country").getAsJsonObject("currency").get("name").getAsString(), true)
                        .addField("Emoji", jsonObject.getAsJsonObject("country").getAsJsonObject("emoji").get("symbol").getAsString(), true);

                String currentTimeString = jsonObject.getAsJsonObject("portable").getAsJsonObject("timezone").get("current_time").getAsString();
                OffsetDateTime offsetDateTime = OffsetDateTime.parse(currentTimeString, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                String formattedTime = offsetDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));

                Button timeZoneButton = Button.secondary("TimeZone", jsonObject.getAsJsonObject("portable").getAsJsonObject("timezone").get("name").getAsString()).asDisabled();
                Button currentTimeButton = Button.secondary("CurrentTime", formattedTime).asDisabled();

                event.replyEmbeds(embedBuilder.build())
                        .setEphemeral(true)
                        .addActionRow(timeZoneButton, currentTimeButton)
                        .queue();
            } else {
                event.reply("Error fetching data from API.").queue();
            }
        } catch (IOException e) {
            e.printStackTrace();
            event.reply("An error occurred while fetching data.").queue();
        }
    }
}
