package com.keychera.cryptemail;

import android.net.Uri;
import android.os.Bundle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.View;
import androidx.core.view.GravityCompat;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import com.keychera.cryptemail.ComposeFragment.OnComposeFragmentInteractionListener;
import com.keychera.cryptemail.EmailFragment.onEmailListFragmentInteraction;
import com.keychera.cryptemail.HelloFragment.OnFragmentInteractionListener;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener, onEmailListFragmentInteraction,
    OnComposeFragmentInteractionListener, OnFragmentInteractionListener {

  private View.OnClickListener ComposeOnClick;
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

    ComposeOnClick = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        navController.navigate(R.id.composeFragment);
      }
    };
    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(ComposeOnClick);
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
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
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
      navController.navigate(R.id.helloFragment);
    } else if (id == R.id.nav_inbox) {
      navController.navigate(R.id.emailFragment);
    } else if (id == R.id.nav_sent) {
      navController.navigate(R.id.emailFragment);
    } else if (id == R.id.nav_drafts) {

    }  else if (id == R.id.nav_settings) {

    }

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  @Override
  public void onEmailItemFragmentInteraction(Email item) {

  }

  @Override
  public void onFragmentInteraction(Uri uri) {

  }
}
