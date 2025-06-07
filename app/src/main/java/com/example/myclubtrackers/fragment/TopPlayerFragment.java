package com.example.myclubtrackers.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myclubtrackers.R;
import com.example.myclubtrackers.databinding.FragmentTopPlayerBinding;
import com.example.myclubtrackers.utils.NetworkUtils;
import com.google.android.material.tabs.TabLayoutMediator;

public class TopPlayerFragment extends Fragment {

    private FragmentTopPlayerBinding binding;
    private TopPlayerPagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTopPlayerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewPager();

        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            showNoNetworkView(true);
        } else {
            showNoNetworkView(false);
        }

        binding.btnRefresh.setOnClickListener(v -> {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                showNoNetworkView(false);
                refreshFragments();
            } else {
                Toast.makeText(requireContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupViewPager() {
        pagerAdapter = new TopPlayerPagerAdapter(requireActivity());
        binding.viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.top_scorers);
                    break;
                case 1:
                    tab.setText(R.string.top_assists);
                    break;
            }
        }).attach();
    }

    private void refreshFragments() {
        int currentItem = binding.viewPager.getCurrentItem();
        pagerAdapter = new TopPlayerPagerAdapter(requireActivity());
        binding.viewPager.setAdapter(pagerAdapter);
        binding.viewPager.setCurrentItem(currentItem);
    }

    private void showNoNetworkView(boolean show) {
        binding.noNetworkView.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.viewPager.setVisibility(show ? View.GONE : View.VISIBLE);
        binding.tabLayout.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private static class TopPlayerPagerAdapter extends FragmentStateAdapter {

        public TopPlayerPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new TopScorerFragment();
            } else {
                return new TopAssistFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

    // Inner fragments for the tabs
    public static class TopScorerFragment extends Fragment {
        // Implementation similar to other fragments but focused on top scorers
    }

    public static class TopAssistFragment extends Fragment {
        // Implementation similar to other fragments but focused on top assists
    }
}