package ru.noties.markwon.extension.emoji.loader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Loads the emojis from a JSON database.
 *
 * @author Vincent DURMONT [vdurmont@gmail.com]
 */
class EmojiLoader {
    private EmojiLoader() {}

    static List<EmojiModel> loadEmojis(InputStream stream) throws IOException {
        try {
            JSONArray emojisJSON = new JSONArray(inputStreamToString(stream));
            List<EmojiModel> emojis = new ArrayList<EmojiModel>(emojisJSON.length());
            for (int i = 0; i < emojisJSON.length(); i++) {
                EmojiModel emoji = buildEmojiFromJSON(emojisJSON.getJSONObject(i));
                if (emoji != null) {
                    emojis.add(emoji);
                }
            }
            return emojis;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private static String inputStreamToString(InputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader isr = new InputStreamReader(stream, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String read;
        while ((read = br.readLine()) != null) {
            sb.append(read);
        }
        br.close();
        return sb.toString();
    }

    private static EmojiModel buildEmojiFromJSON(JSONObject json) throws Exception {
        if (!json.has("emoji")) {
            return null;
        }
        byte[] bytes = json.getString("emoji").getBytes("UTF-8");
        String description = null;
        if (json.has("description")) {
            description = json.getString("description");
        }
        boolean supportsFitzpatrick = false;
        if (json.has("supports_fitzpatrick")) {
            supportsFitzpatrick = json.getBoolean("supports_fitzpatrick");
        }
        List<String> aliases = jsonArrayToStringList(json.getJSONArray("aliases"));
        List<String> tags = jsonArrayToStringList(json.getJSONArray("tags"));
        return new EmojiModel(description, supportsFitzpatrick, aliases, tags, bytes);
    }

    private static List<String> jsonArrayToStringList(JSONArray array) {
        List<String> strings = new ArrayList<String>(array.length());
        try {
            for (int i = 0; i < array.length(); i++) {
                strings.add(array.getString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strings;
    }
}
