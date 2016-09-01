package com.mts2792.samplenavdrawer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private EditText ip;
    private EditText aip;

    public String   s_dns1 ;
    public String   s_dns2;
    public String   s_gateway;
    public String   s_ipAddress;
    public String   s_leaseDuration;
    public String   s_netmask;
    public String   s_serverAddress;
    TextView info;
    DhcpInfo d;
    WifiManager wifii;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Display SSID

        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String ssid = info.getSSID();
        TextView ssidTextView = (TextView) findViewById(R.id.infolbl);
        ssidTextView.setText(ssid);

        // External IP and Internal EditText

        ip = (EditText) findViewById(R.id.extip);
        aip = (EditText) findViewById(R.id.androidip);


//        Button btnCopyExtIP = (Button) findViewById(R.id.btnExtIP);
        ImageButton btnCopyExtIP = (ImageButton) findViewById(R.id.btnExtIP);
        btnCopyExtIP.setOnClickListener(this);

//        Button btnCopyIntIP = (Button) findViewById(R.id.btnIntfIP);
        ImageButton btnCopyIntIP = (ImageButton) findViewById(R.id.btnIntIP);
        btnCopyIntIP.setOnClickListener(this);

        updateIP();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.about:
                AboutDialog.show(this);
                return true;
            case R.id.action_refresh:
                updateIP();
//                startActivity(new Intent(this, Settings.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(this, SettingsDialog.class));

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateIP() {
        final SimpleHttpTask verTask = new SimpleHttpTask(this);
        verTask.execute();
        final AndIPTask androidip = new AndIPTask();
        androidip.execute();
    }

    private void copyExtIP() {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setText(ip.getText());
    }

    private void copyIntfIP() {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setText(aip.getText());
    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnExtIP:
                copyExtIP();
                Snackbar.make(findViewById(android.R.id.content),
                        getString(R.string.toast_copied).trim()+ " " + ip.getText() + " ",Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE)
                        .setAction("DISMISS", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        })
                        .show();
                break;

            case R.id.btnIntIP:
                copyIntfIP();
                Snackbar.make(findViewById(android.R.id.content),
                        getString(R.string.toast_copied).trim()+ " " + aip.getText() + " ",Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE)
                        .setAction("DISMISS", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        })
                        .show();
                break;
        }
    }

    private class AndIPTask extends AsyncTask<Void, Void, Void> {
        // android IP
        String andIP;

        @Override
        protected void onPreExecute() {
            aip.setText(getString(R.string.info_please_wait));
            return;
        }

        protected Void doInBackground(Void... arg0) {
            andIP = getlocalIP();
            return null;
        }

        @Override
        protected void onPostExecute(Void arg) {
            if (andIP == null) {
                aip.setText(getString(R.string.info_error));
            } else {
                aip.setText(andIP);
            }
            Snackbar.make(findViewById(android.R.id.content), "Up to date", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE)
                    .setAction("DISMISS", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    })
                    .show();
            return;
        }
    }

    private class SimpleHttpTask extends AsyncTask<Void, Void, Void> {
        private final Context context;
        // android IP
        String extIP;

        public SimpleHttpTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            ip.setText(getString(R.string.info_please_wait));

            return;
        }

        @Override
        protected Void doInBackground(Void... params) {
            extIP = getCurrentIP(context);
            return null;
        }

        @Override
        protected void onPostExecute(Void arg) {
            ip.setText(extIP);

            Snackbar.make(findViewById(android.R.id.content), "External IP retrieved", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE)
                    .setAction("DISMISS", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    })
                    .show();
            return;
        }

    }

    private String getlocalIP() {
        try {
            String interfaces = "";
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        interfaces = interfaces
                                + inetAddress.getHostAddress().toString();
                    }
                }
            }
            return (interfaces);
        } catch (SocketException ex) {
            Log.i("externalip", ex.toString());
        }
        return null;
    }

    private String getCurrentIP(Context context) {
        String useurl;
        String remoteurl;

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        remoteurl = prefs.getString("remoteurl","");
        if (remoteurl == "") {
            useurl = prefs.getString("remoteurllist",
                    "http://icanhazip.com/");
        } else {
            useurl = remoteurl;
        }

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(useurl);
            HttpResponse response;

            response = httpclient.execute(httpget);

            // Log.i("externalip",response.getStatusLine().toString());

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                long len = entity.getContentLength();
                if (len != -1 && len < 1024) {
                    String str = EntityUtils.toString(entity).toString().trim();
                    // Log.i("externalip",str);
                    return (str);
                } else {
                    return (getString(R.string.info_response_long)).toString().trim();
                    // debug
                    // ip.setText("Response too long or error: "+EntityUtils.toString(entity));
                    // Log.i("externalip",EntityUtils.toString(entity));
                }
            } else {
                return (getString(R.string.info_error) + response
                        .getStatusLine().toString());
            }

        } catch (Exception e) {
            return (getString(R.string.info_error));
        }
    }
}
