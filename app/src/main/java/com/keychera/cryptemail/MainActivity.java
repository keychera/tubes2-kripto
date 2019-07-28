package com.keychera.cryptemail;

import android.net.Uri;
import android.os.Bundle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.core.view.GravityCompat;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.snackbar.Snackbar;
import com.keychera.cryptemail.ComposeFragment.OnComposeFragmentInteractionListener;
import com.keychera.cryptemail.EmailFragment.onEmailListFragmentInteraction;
import com.keychera.cryptemail.HelloFragment.OnFragmentInteractionListener;

public class MainActivity extends AppCompatActivity
    implements OnNavigationItemSelectedListener, onEmailListFragmentInteraction,
    OnComposeFragmentInteractionListener, OnFragmentInteractionListener{

  private NavController navController;
  private AppBarConfiguration appBarConfiguration;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    NavigationView navigationView = findViewById(R.id.nav_view);

    navController = Navigation.findNavController(this, R.id.nav_host_fragment);
    appBarConfiguration = new AppBarConfiguration.Builder(R.id.helloFragment, R.id.emailFragment)
            .setDrawerLayout(drawer)
            .build();

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    NavigationUI.setupActionBarWithNavController(this, this.navController, this.appBarConfiguration);

    NavigationUI.setupWithNavController(navigationView, navController);
    navigationView.setNavigationItemSelectedListener(this);
  }

  @Override
  public boolean onSupportNavigateUp() {
    return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
  }

  @Override
  public void onBackPressed() {
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.nav_home) {
      navController.navigate(R.id.helloFragment);
    } else if (id == R.id.nav_inbox) {
      navController.navigate(R.id.emailFragment);
    } else if (id == R.id.nav_sent) {
      navController.navigate(R.id.emailFragment);
    } else if (id == R.id.nav_drafts) {
      navController.navigate(R.id.emailFragment);
    }  else if (id == R.id.nav_settings) {
      Snackbar.make(findViewById(android.R.id.content), "Not Yet Implemented", Snackbar.LENGTH_LONG)
          .setAction("Action", null).show();
    }

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  @Override
  public void onEmailItemFragmentInteraction(SimpleEmail item) {

  }

  @Override
  public void onFragmentInteraction(Uri uri) {

  }
}
