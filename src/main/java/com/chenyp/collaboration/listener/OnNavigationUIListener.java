package com.chenyp.collaboration.listener;

import android.os.Bundle;

import com.chenyp.collaboration.ui.fragment.achievement.details.AchievementDetailAddFragment;
import com.chenyp.collaboration.ui.fragment.achievement.details.AchievementDetailModifyFragment;
import com.chenyp.collaboration.ui.fragment.selectPhoto.BrowsePhotoFragment;
import com.chenyp.collaboration.ui.fragment.selectPhoto.SelectPhotoFragment;

import java.util.List;

/**
 * Created by change on 2015/10/30.
 */
public interface OnNavigationUIListener {

    void goBackFromCurrentPage();

    void goBackCommunicationTopicShowPage(String tag);

    void navigateToLoginPage();

    void navigateToRegisterPage();

    void navigateToAchievementShowPage(String exhibit);

    void navigateToAchievementShowPage(String exhibit, boolean modifyMode);

    void navigateToAchievementAddPage();

    void navigateToAchievementModifyPage(Bundle bundle);

    void navigateToAchievementDetailShowPage(String json, int position);

    void navigateToAchievementDetailShowPage(String json, int position, boolean modifyMode);

    void navigateToAchievementDetailAddPage();

    void navigateToAchievementDetailAddPage(String exhibit);

    void navigateToAchievementDetailAddPage(AchievementDetailAddFragment.OnDetailAddListener onDetailAddListener);

    void navigateToAchievementDetailModifyPage(String json, int position, AchievementDetailModifyFragment.OnDetailModifyListener onDetailModifyListener);

    void navigateToCommunicationAddPage();

    void navigateToCommunicationShowPage(String json);

    void navigateToCommunicationShowPage(String json, boolean modifyMode);

    void navigateToCommunicationModifyPage(Bundle bundle);

    void navigateToCommunicationTopicAddPage(String parentJson);

    void navigateToCommunicationTopicShowPage(String parentJson, String parentListJson);

    void navigateToCommunicationFilePage(Bundle bundle);

    void navigateToBrowsePhotoPage(List<String> urls, List<String> selectUrls, int position, int modeCode, BrowsePhotoFragment.OnModifyFinishListener onModifyFinishListener);

    void navigateToSelectPhotoPage(int maxCount, boolean showCamera, SelectPhotoFragment.OnSelectPhotoFinishListener onSelectPhotoFinishListener);
}
