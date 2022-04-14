package com.romanpulov.odeon;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.romanpulov.odeon.databinding.MainActivityBinding;
import com.romanpulov.odeon.worker.LoadManager;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_FILE = 2;

    private static void log(String message) {
        Log.d(MainActivity.class.getSimpleName(), message);
    }

    private NavController mNavController;
    private AppBarConfiguration mAppBarConfiguration;

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    LoadViewModel loadViewModel = (new ViewModelProvider(this)).get(LoadViewModel.class);
                    loadViewModel.setUri(uri);
                    mNavController.navigate(R.id.action_artistsFragment_to_loadFragment);

                    LoadManager.startDownloadFromUri(getApplicationContext(), uri);

                    //LoadManager.startDownloadFromUri(getApplicationContext(), uri);
                }
                // Handle the returned Uri
                /*
                try (
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        OutputStream outputStream = openFileOutput(ARCHIVE_FILE_NAME, MODE_PRIVATE);
                )
                {
                    FileUtils.copyStream(inputStream, outputStream);
                    displayMessage("File loaded");
                } catch (IOException e) {
                    displayMessage("File not loaded:" + e.getMessage());
                    e.printStackTrace();
                }

                 */
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivityBinding binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) binding.navHostFragment.getFragment();
        mNavController = navHostFragment.getNavController();

        mAppBarConfiguration =
                new AppBarConfiguration.Builder(mNavController.getGraph())
                        .setOpenableLayout(binding.drawerLayout)
                        .build();

        NavigationUI.setupWithNavController(
                toolbar, mNavController, mAppBarConfiguration);

        binding.navView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.loadFragment) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");

                mGetContent.launch("*/*");
                /*
                mNavController.navigate(R.id.action_artistsFragment_to_downloadFragment);

                 */
            }
            return true;
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return NavigationUI.onNavDestinationSelected(item, mNavController)
                || super.onOptionsItemSelected(item);
    }
}