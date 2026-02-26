package com.example.foodplanner.presentation.splash;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.foodplanner.R;
import com.example.foodplanner.data.dataSource.local.SessionManager;
import com.example.foodplanner.databinding.FragmentSplashBinding;

public class SplashFragment extends Fragment {

    private FragmentSplashBinding binding;

    public SplashFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSplashBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.lottieSplash.playAnimation();

        binding.lottieSplash.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                SessionManager sessionManager =
                        new SessionManager(requireContext());

                if (sessionManager.isLoggedIn()) {

                    NavHostFragment.findNavController(SplashFragment.this)
                            .navigate(R.id.action_splashFragment2_to_homeFragment);

                } else {

                    NavHostFragment.findNavController(SplashFragment.this)
                            .navigate(R.id.action_splashFragment2_to_loginFragment);
                }
                Log.d("SplashDebug", "isLoggedIn = " + sessionManager.isLoggedIn());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}