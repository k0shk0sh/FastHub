package ru.noties.markwon;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import ru.noties.markwon.spans.LinkSpan;

public class LinkResolverDef implements LinkSpan.Resolver {
    @Override
    public void resolve(View view, @NonNull String link) {
        final Uri uri = Uri.parse(link);
        final Context context = view.getContext();
        final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.w("LinkResolverDef", "Actvity was not found for intent, " + intent.toString());
        }
    }
}
