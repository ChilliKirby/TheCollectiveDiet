package com.example.thecollectivediet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.thecollectivediet.API_Utilities.User_API_Controller;
import com.example.thecollectivediet.API_Utilities.VolleyResponseListener;
import com.example.thecollectivediet.Camera_Fragment_Components.CameraFragment;
import com.example.thecollectivediet.Intro.IntroActivity;
import com.example.thecollectivediet.JSON_Marshall_Objects.User;
import com.example.thecollectivediet.Me_Fragment_Components.MeTabLayoutFragment;
import com.example.thecollectivediet.Profile_Fragment_Components.ProfileFragment;
import com.example.thecollectivediet.Us_Fragment_Components.UsFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.Map;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    GoogleSignInClient mGoogleSignInClient;

    //elements
    Toolbar toolbar;
    DrawerLayout drawer;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Nullable
    public static User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Map<String, String> env = System.getenv();
        setContentView(R.layout.activity_main);

        prefs = this.getSharedPreferences("TheCollectiveDiet", Context.MODE_PRIVATE);
        editor = prefs.edit();

        TextView login = findViewById(R.id.toolbar_login);
        login.setOnClickListener(v -> {
            if (!isSignedIn())
                commitFragmentTransaction(this, R.id.fragmentHolder, new FragmentSignIn());
        });

        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        String username = prefs.getString("user", "null");
                        login.setText(username);

                    } else {
                        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail()
                                .build();
                        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
                        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);

                        if (account != null && !account.isExpired()) {
                            String username = prefs.getString("user", "null");
                            login.setText(username);
                        }
                    }
                });

        //If this is user's first time on app
        String firstTime = prefs.getString("firstTime", "null");

        if (firstTime.equals("null")) {
            editor.putString("firstTime", "false");
            editor.commit();

            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);

        if (account != null && !account.isExpired()) {

            commitFragmentTransaction(MainActivity.this, R.id.fragmentHolder, new FragmentSplashScreen());
            String username = prefs.getString("user", "null");
            login.setText(username);


            User_API_Controller.handleNewSignIn(account, MainActivity.this, new VolleyResponseListener<User>() {
                @Override
                public void onResponse(User user) {
                    MainActivity.setCurrentUser(user);
                    commitFragmentTransaction(MainActivity.this, R.id.fragmentHolder, new MeTabLayoutFragment());
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });


            //todo
            //get user metrics

        } else
            commitFragmentTransaction(this, R.id.fragmentHolder, new FragmentSignIn());

        //Setup button, views, etc in the activity_main layout
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //Bottom navigation tool bar on the bottom of the app screen will be used for
        //navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_toolbar);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {

            //When icon in bottom app is selected, switch to appropriate fragment
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.bottom_nav_me)
                    commitFragmentTransaction(MainActivity.this, R.id.fragmentHolder, new MeTabLayoutFragment());
                else if (id == R.id.bottom_nav_camera)
                    commitFragmentTransaction(MainActivity.this, R.id.fragmentHolder, new CameraFragment());
                else if (id == R.id.bottom_nav_profile)
                    commitFragmentTransaction(MainActivity.this, R.id.fragmentHolder, new ProfileFragment());
                else if (id == R.id.bottom_nav_us)
                    commitFragmentTransaction(MainActivity.this, R.id.fragmentHolder, new UsFragment());

                return true;
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }


    @Override
    public void onBackPressed() {
        //If the drawer is open, close it
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        //else, if the drawer is closed, rely on super class default behavior
        else {
            // TODO: fix this - super.onBackPressed();
            commitFragmentTransaction(this, R.id.fragmentHolder, new MeTabLayoutFragment());
        }
    }

    //Navigation via the drawer
    //Handle navigation view item clicks here
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        //Create a transaction
        int id = item.getItemId();
        Fragment fragment = null;

        //Create a new fragment of the appropriate type depending
        //on click item
        if (id == R.id.nav_food)
            fragment = new CameraFragment();
        else if (id == R.id.nav_goals)
            commitFragmentTransaction(MainActivity.this, R.id.fragmentHolder, new MeTabLayoutFragment(2));
        else if (id == R.id.nav_profile)
            fragment = new ProfileFragment();
        else if (id == R.id.nav_us)
            fragment = new UsFragment();
        else if (id == R.id.nav_me)
            fragment = new MeTabLayoutFragment();
        else if (id == R.id.nav_sign_in && !isSignedIn())
            fragment = new FragmentSignIn();
        else if (id == R.id.nav_sign_out) {
            signOut();
            fragment = new FragmentSignIn();

            TextView login = findViewById(R.id.toolbar_login);
            login.setText("sign in");
        }

        if (fragment != null)
            commitFragmentTransaction(this, R.id.fragmentHolder, fragment);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @NonNull
    @Override
    public FragmentManager getSupportFragmentManager() {
        hideKeyboard(this);
        return super.getSupportFragmentManager();
    }

    public static void hideKeyboard(@NonNull Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(this) != null && !GoogleSignIn.getLastSignedInAccount(this).isExpired();
    }

    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                currentUser = null;
            }
        });
    }

    @Nullable
    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public void requireSignInPrompt(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        commitFragmentTransaction(this, R.id.fragmentHolder, new FragmentSignIn());
    }

    public static void commitFragmentTransaction(@NonNull FragmentActivity activity,
                                                 int fragmentHolderID, @NonNull Fragment fragment) {
        FragmentTransaction transaction = Objects.requireNonNull(activity.getSupportFragmentManager().beginTransaction());
        transaction.replace(fragmentHolderID, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
