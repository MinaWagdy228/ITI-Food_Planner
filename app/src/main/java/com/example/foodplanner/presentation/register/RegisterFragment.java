package com.example.foodplanner.presentation.register;

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
import com.example.foodplanner.databinding.FragmentRegisterBinding;

import java.util.Objects;

public class RegisterFragment extends Fragment implements ViewRegister {

    private FragmentRegisterBinding binding;
    private RegisterPresenter presenter;

    public RegisterFragment() {
        super(R.layout.fragment_register);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new RegisterPresenterImp(this, requireContext());

        binding.btnRegister.setOnClickListener(v -> {
            presenter.register(
                    Objects.requireNonNull(binding.etName.getText()).toString().trim(),
                    Objects.requireNonNull(binding.etRegisterEmail.getText()).toString().trim(),
                    Objects.requireNonNull(binding.etRegisterPassword.getText()).toString().trim(),
                    Objects.requireNonNull(binding.etConfirmPassword.getText()).toString().trim()
            );
        });
    }

    @Override
    public void onRegisterSuccess() {
        // Navigate to Home after successful register
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_registerFragment_to_loginFragment);
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