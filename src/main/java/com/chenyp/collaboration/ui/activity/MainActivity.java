package com.chenyp.collaboration.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.chenyp.collaboration.R;
import com.chenyp.collaboration.listener.OnNavigationUIListener;
import com.chenyp.collaboration.ui.fragment.achievement.AchievementAddFragment;
import com.chenyp.collaboration.ui.fragment.achievement.details.AchievementDetailAddFragment;
import com.chenyp.collaboration.ui.fragment.achievement.details.AchievementDetailModifyFragment;
import com.chenyp.collaboration.ui.fragment.achievement.details.AchievementDetailShowFragment;
import com.chenyp.collaboration.ui.fragment.achievement.AchievementModifyFragment;
import com.chenyp.collaboration.ui.fragment.achievement.AchievementShowFragment;
import com.chenyp.collaboration.ui.fragment.selectPhoto.BrowsePhotoFragment;
import com.chenyp.collaboration.ui.fragment.communication.CommunicationAddFragment;
import com.chenyp.collaboration.ui.fragment.communication.CommunicationFileFragment;
import com.chenyp.collaboration.ui.fragment.communication.CommunicationModifyFragment;
import com.chenyp.collaboration.ui.fragment.communication.CommunicationShowFragment;
import com.chenyp.collaboration.ui.fragment.communication.topic.CommunicationTopicAddFragment;
import com.chenyp.collaboration.ui.fragment.communication.topic.CommunicationTopicShowFragment;
import com.chenyp.collaboration.ui.fragment.LoginFragment;
import com.chenyp.collaboration.ui.fragment.MainFragment;
import com.chenyp.collaboration.ui.fragment.RegisterFragment;
import com.chenyp.collaboration.ui.fragment.selectPhoto.SelectPhotoFragment;

import java.util.List;

import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements OnNavigationUIListener {

    @Override
    protected int getContainerResId() {
        return R.id.id_fl_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        this.replaceFragment(MainFragment.newInstance(new Bundle()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onSupportNavigateUp() {
        goBackFromCurrentPage();
        return true;
    }

    @Override
    public void goBackFromCurrentPage() {
        popBackStack();
    }

    @Override
    public void goBackCommunicationTopicShowPage(String tag) {
        popBackStack(tag);
    }

    @Override
    public void navigateToLoginPage() {
        replaceFragmentAndAddBackStack(LoginFragment.newInstance());
    }

    @Override
    public void navigateToRegisterPage() {
        replaceFragmentAndAddBackStack(RegisterFragment.newInstance());
    }

    @Override
    public void navigateToAchievementShowPage(String exhibit) {
        replaceFragmentAndAddBackStack(AchievementShowFragment.newInstance(exhibit));
    }

    @Override
    public void navigateToAchievementShowPage(String exhibit, boolean modifyMode) {
        replaceFragmentAndAddBackStack(AchievementShowFragment.newInstance(exhibit, modifyMode));
    }

    @Override
    public void navigateToBrowsePhotoPage(List<String> urls, List<String> selectUrls, int position
            , int modeCode, BrowsePhotoFragment.OnModifyFinishListener onModifyFinishListener) {
        BrowsePhotoFragment fragment = BrowsePhotoFragment.newInstance(urls, selectUrls, position, modeCode);
        fragment.setOnModifyFinishListener(onModifyFinishListener);
        replaceFragmentAndAddBackStack(fragment);
    }

    @Override
    public void navigateToSelectPhotoPage(int maxCount, boolean showCamera,
                                          SelectPhotoFragment.OnSelectPhotoFinishListener onSelectPhotoFinishListener) {
        SelectPhotoFragment fragment = SelectPhotoFragment.newInstance(maxCount, showCamera);
        fragment.setOnSelectPhotoFinishListener(onSelectPhotoFinishListener);
        replaceFragmentAndAddBackStack(fragment);
    }

    @Override
    public void navigateToAchievementAddPage() {
        replaceFragmentAndAddBackStack(AchievementAddFragment.newInstance());
    }

    @Override
    public void navigateToAchievementModifyPage(Bundle bundle) {
        replaceFragmentAndAddBackStack(AchievementModifyFragment.newInstance(bundle));
    }

    @Override
    public void navigateToAchievementDetailShowPage(String json, int position) {
        replaceFragmentAndAddBackStack(AchievementDetailShowFragment.newInstance(json, position));
    }

    @Override
    public void navigateToAchievementDetailShowPage(String json, int position, boolean modifyMode) {
        replaceFragmentAndAddBackStack(AchievementDetailShowFragment.newInstance(json, position, modifyMode));
    }

    @Override
    public void navigateToAchievementDetailAddPage() {
        replaceFragmentAndAddBackStack(AchievementDetailAddFragment.newInstance());
    }

    @Override
    public void navigateToAchievementDetailAddPage(String exhibit) {
        replaceFragmentAndAddBackStack(AchievementDetailAddFragment.newInstance(exhibit));
    }

    @Override
    public void navigateToAchievementDetailAddPage(AchievementDetailAddFragment.OnDetailAddListener onDetailAddListener) {
        AchievementDetailAddFragment fragment = AchievementDetailAddFragment.newInstance();
        fragment.setOnDetailSubmitListener(onDetailAddListener);
        replaceFragmentAndAddBackStack(fragment);
    }

    @Override
    public void navigateToAchievementDetailModifyPage(String json, int position, AchievementDetailModifyFragment.OnDetailModifyListener onDetailSubmitListener) {
        AchievementDetailModifyFragment fragment = AchievementDetailModifyFragment.newInstance(json, position);
        fragment.setOnDetailSubmitListener(onDetailSubmitListener);
        replaceFragmentAndAddBackStack(fragment);
    }

    @Override
    public void navigateToCommunicationAddPage() {
        replaceFragmentAndAddBackStack(CommunicationAddFragment.newInstance());
    }

    @Override
    public void navigateToCommunicationShowPage(String json) {
        replaceFragmentAndAddBackStack(CommunicationShowFragment.newInstance(json));
    }

    @Override
    public void navigateToCommunicationShowPage(String json, boolean modifyMode) {
        replaceFragmentAndAddBackStack(CommunicationShowFragment.newInstance(json, modifyMode));
    }

    @Override
    public void navigateToCommunicationModifyPage(Bundle bundle) {
        replaceFragmentAndAddBackStack(CommunicationModifyFragment.newInstance(bundle));
    }

    @Override
    public void navigateToCommunicationTopicAddPage(String parentJson) {
        replaceFragmentAndAddBackStack(CommunicationTopicAddFragment.newInstance(parentJson));
    }

    @Override
    public void navigateToCommunicationTopicShowPage(String json, String parentJson) {
        replaceFragmentAndAddBackStack(CommunicationTopicShowFragment.newInstance(json, parentJson));
    }

    @Override
    public void navigateToCommunicationFilePage(Bundle bundle) {
        replaceFragmentAndAddBackStack(CommunicationFileFragment.newInstance(bundle));
    }

}
