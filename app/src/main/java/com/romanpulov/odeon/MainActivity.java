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
import androidx.work.Data;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.romanpulov.odeon.databinding.MainActivityBinding;
import com.romanpulov.odeon.helper.DisplayMessageHelper;
import com.romanpulov.odeon.worker.DownloadWorker;
import com.romanpulov.odeon.worker.LoadManager;
import com.romanpulov.odeon.worker.ProcessWorker;

import java.util.List;
import java.util.Map;

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

                    LoadViewModel.LoadProgress loadProgress = loadViewModel.getLoadProgress().getValue();
                    loadProgress.getLoadSteps().put(
                            LoadViewModel.StepType.DOWNLOAD,
                            new LoadViewModel.LoadStep(LoadViewModel.LoadStatus.RUNNING, new Bundle())
                    );
                    LoadManager.startDownloadFromUri(getApplicationContext(), uri);
                }
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

                LoadViewModel loadViewModel = new ViewModelProvider(this).get(LoadViewModel.class);
                LoadViewModel.LoadProgress loadProgress = loadViewModel.getLoadProgress().getValue();
                if (loadProgress != null && loadProgress.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");

                    mGetContent.launch("*/*");
                } else {
                    mNavController.navigate(R.id.action_artistsFragment_to_loadFragment);
                }
            }
            return true;
        });

        setupWorkManagerObserver();
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

    private void setupWorkManagerObserver() {

        WorkManager
                .getInstance(this)
                .getWorkInfosByTagLiveData(LoadManager.WORK_TAG_DOWNLOAD)
                .observe(this, workInfos -> {
                    if (workInfos.size() > 0) {
                        handleDownloadWork(workInfos.get(0));
                    }
                });

        WorkManager
                .getInstance(this)
                .getWorkInfosByTagLiveData(LoadManager.WORK_TAG_PROCESS)
                .observe(this, workInfos -> {
                    if (workInfos.size() > 0) {
                        handleProcessWork(workInfos.get(0));
                    }
                });
    }

    private void handleDownloadWork(WorkInfo workInfo) {
        LoadViewModel loadViewModel = new ViewModelProvider(this).get(LoadViewModel.class);
        LoadViewModel.LoadProgress loadProgress = loadViewModel.getLoadProgress().getValue();
        if (loadProgress != null) {
            Map<LoadViewModel.StepType, LoadViewModel.LoadStep> loadSteps = loadProgress.getLoadSteps();

            if (loadSteps.containsKey(LoadViewModel.StepType.DOWNLOAD)) {

                Bundle params = new Bundle();

                if (workInfo.getState() == WorkInfo.State.RUNNING) {
                    log("Running");
                    loadSteps.clear();

                    params.putLong(
                            LoadViewModel.PARAM_NAME_VALUE,
                            workInfo.getProgress().getLong(DownloadWorker.PARAM_NAME_PROGRESS_CURRENT, 0)
                    );
                    params.putLong(
                            LoadViewModel.PARAM_NAME_MAX_VALUE,
                            workInfo.getProgress().getLong(DownloadWorker.PARAM_NAME_PROGRESS_TOTAL, 0)
                    );
                    loadSteps.put(LoadViewModel.StepType.DOWNLOAD,
                            new LoadViewModel.LoadStep(LoadViewModel.LoadStatus.RUNNING, params)
                    );
                } else if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                    params.putLong(
                            LoadViewModel.PARAM_NAME_VALUE,
                            100
                    );
                    params.putLong(
                            LoadViewModel.PARAM_NAME_MAX_VALUE,
                            100
                    );
                    loadSteps.put(LoadViewModel.StepType.DOWNLOAD,
                            new LoadViewModel.LoadStep(LoadViewModel.LoadStatus.COMPLETED, params)
                    );
                    loadProgress.getLoadSteps().put(LoadViewModel.StepType.PASSWORD_REQUEST, new LoadViewModel.LoadStep(LoadViewModel.LoadStatus.WAITING, null));
                    DisplayMessageHelper.displayInfoMessage(this, getString(R.string.notification_file_downloaded));
                } else if (workInfo.getState() == WorkInfo.State.CANCELLED) {
                    log("Cancelled");
                    loadProgress = new LoadViewModel.LoadProgress();
                } else if (workInfo.getState() == WorkInfo.State.FAILED) {
                    log("Failed");
                }

                loadViewModel.getLoadProgress().postValue(loadProgress);
            }
        }
    }

    private void handleProcessWork(WorkInfo workInfo) {
        LoadViewModel loadViewModel = new ViewModelProvider(this).get(LoadViewModel.class);
        LoadViewModel.LoadProgress loadProgress = loadViewModel.getLoadProgress().getValue();
        if (loadProgress != null) {
            Map<LoadViewModel.StepType, LoadViewModel.LoadStep> loadSteps = loadProgress.getLoadSteps();
            log("Process work info:" + workInfo);

            if (loadSteps.containsKey(LoadViewModel.StepType.PROCESS)) {
                if (workInfo.getState() == WorkInfo.State.RUNNING) {
                    String message = workInfo.getProgress().getString(ProcessWorker.PARAM_NAME_MESSAGE);
                    if (message != null && !message.isEmpty()) {
                        loadViewModel.getMessage().postValue(message);
                    }

                } else if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                    DisplayMessageHelper.displayInfoMessage(this, getString(R.string.notification_successfully_completed));
                    loadProgress = new LoadViewModel.LoadProgress();
                    loadViewModel.getLoadProgress().postValue(loadProgress);

                } else if (workInfo.getState() == WorkInfo.State.FAILED) {
                    loadSteps.remove(LoadViewModel.StepType.PROCESS);
                    loadSteps.put(LoadViewModel.StepType.PASSWORD_REQUEST, new LoadViewModel.LoadStep(LoadViewModel.LoadStatus.WAITING, null));
                    String errorMessage = workInfo.getOutputData().getString(ProcessWorker.PARAM_NAME_MESSAGE);
                    if (errorMessage != null && !errorMessage.isEmpty()) {
                        DisplayMessageHelper.displayErrorMessage(this, errorMessage);
                    }
                    loadViewModel.getLoadProgress().postValue(loadProgress);

                } else if (workInfo.getState() == WorkInfo.State.CANCELLED) {
                    loadSteps.remove(LoadViewModel.StepType.PROCESS);
                    loadSteps.put(LoadViewModel.StepType.PASSWORD_REQUEST, new LoadViewModel.LoadStep(LoadViewModel.LoadStatus.WAITING, null));
                    loadViewModel.getLoadProgress().postValue(loadProgress);

                }
            }
        }
    }
}