package com.fastaccess.ui.modules.profile.banner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Button;

import com.fastaccess.App;
import com.fastaccess.R;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.FileHelper;
import com.fastaccess.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

/**
 * Created by JediB on 5/25/2017.
 */

public class BannerInfoActivity extends BaseActivity<BannerInfoMvp.View, BannerInfoPresenter> implements BannerInfoMvp.View {

    @BindView(R.id.imageChooser) Button imageChooser;

    @Override protected int layout() {
        return R.layout.activity_banner_info;
    }

    @Override protected boolean isTransparent() {
        return false;
    }

    @Override protected boolean canBack() {
        return true;
    }

    @Override protected boolean isSecured() {
        return false;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @OnClick(R.id.imageChooser) void onChooseImage() {
        if (ActivityHelper.checkAndRequestReadWritePermission(this)) {
            showFileChooser();
        }
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BundleConstant.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String path = FileHelper.getPath(this, data.getData());
                if (path == null) {
                    showMessage(R.string.error, R.string.image_error);
                    return;
                }
                getPresenter().onPostImage(path);
            }

            finish();
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showFileChooser();
                } else {
                    Toasty.error(App.getInstance(), getString(R.string.permission_failed)).show();
                }
            }
        }
    }

    @NonNull @Override public BannerInfoPresenter providePresenter() {
        return new BannerInfoPresenter();
    }

    @Override public void onFinishedUploading() {
        showMessage(R.string.success, R.string.successfully_submitted);
        setResult(RESULT_OK);
        finish();
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), BundleConstant.REQUEST_CODE);
    }
}
