package org.wordpress.android.ui.stats;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import org.wordpress.android.R;
import org.wordpress.android.WordPress;
import org.wordpress.android.analytics.AnalyticsTracker;
import org.wordpress.android.fluxc.model.SiteModel;
import org.wordpress.android.fluxc.store.AccountStore;
import org.wordpress.android.ui.JetpackConnectionWebViewActivity;

import javax.inject.Inject;

import static org.wordpress.android.WordPress.SITE;
import static org.wordpress.android.ui.JetpackConnectionWebViewActivity.JETPACK_CONNECTION_DEEPLINK;

/**
 * An activity that shows when user tries to open Stats without Jetpack connected.
 * It offers a link to the Jetpack connection flow.
 */

public class StatsConnectJetpackActivity extends AppCompatActivity {

    @Inject
    AccountStore mAccountStore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((WordPress) getApplication()).component().inject(this);

        setContentView(R.layout.stats_jetpack_connection_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
            actionBar.setTitle(R.string.stats);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle(R.string.stats);

        Button loginButton = findViewById(R.id.jetpack_setup);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startJetpackConnectionFlow();
            }
        });
    }

    private void startJetpackConnectionFlow() {
        SiteModel site = (SiteModel) getIntent().getSerializableExtra(SITE);
        String stringToLoad = "https://wordpress.com/jetpack/connect?"
                + "url=" + site.getUrl()
                + "&mobile_redirect="
                + JETPACK_CONNECTION_DEEPLINK;
        if (mAccountStore.hasAccessToken()) {
            JetpackConnectionWebViewActivity.openJetpackConnectionFlow(StatsConnectJetpackActivity.this, stringToLoad, site);
        } else {
            JetpackConnectionWebViewActivity.openUnauthorizedJetpackConnectionFlow(StatsConnectJetpackActivity.this, stringToLoad, site);
        }
        finish();
        if (!site.isJetpackInstalled()) {
            AnalyticsTracker.track(AnalyticsTracker.Stat.STATS_SELECTED_INSTALL_JETPACK);
        } else {
            AnalyticsTracker.track(AnalyticsTracker.Stat.STATS_SELECTED_CONNECT_JETPACK);
        }
    }

}
