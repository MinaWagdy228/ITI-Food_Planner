package com.example.foodplanner.presentation.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.foodplanner.R;
import com.example.foodplanner.databinding.FragmentProfileBinding;
import com.example.foodplanner.db.UserEntity;

public class ProfileFragment extends Fragment implements ViewProfile {

    private FragmentProfileBinding binding;
    private ProfilePresenter presenter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        presenter = new ProfilePresenterImp(this, requireContext());

        presenter.loadUserData();

        binding.btnLogout.setOnClickListener(v -> presenter.logout());

        binding.btnDeleteAccount.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage("This action cannot be undone. Are you sure?")
                .setPositiveButton("Delete", (dialog, which) -> presenter.deleteAccount())
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void showUserData(UserEntity user) {
        binding.tvUserName.setText(user.getName());
        binding.tvUserEmail.setText(user.getEmail());
    }


    @Override
    public void navigateToLogin() {
        NavHostFragment.findNavController(this)
                .navigate(R.id.loginFragment);
    }

    @Override
    public void showError(String message) {
        // Optional: Snackbar later
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDestroy();
        binding = null;
    }
}