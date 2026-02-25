package com.example.foodplanner.presentation.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.foodplanner.R;
import com.example.foodplanner.databinding.FragmentLoginBinding;

import java.util.Objects;

public class LoginFragment extends Fragment implements ViewLogin {

    private FragmentLoginBinding binding;
    private LoginPresenter presenter;

    public LoginFragment() {
        super(R.layout.fragment_login);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new LoginPresenterImp(this, requireContext());

        binding.btnLogin.setOnClickListener(v -> {
            presenter.login(
                    Objects.requireNonNull(binding.etEmail.getText()).toString().trim(),
                    Objects.requireNonNull(binding.etPassword.getText()).toString().trim()
            );
        });

        // Navigate to Register screen
        binding.tvRegister.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_loginFragment_to_registerFragment);
        });
    }

    @Override
    public void onLoginSuccess() {
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_loginFragment_to_homeFragment);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.onDestroy();
        }
        binding = null;
    }
}