package com.fastaccess.ui.modules.profile.banner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.fastaccess.R;
import com.fastaccess.data.dao.CreateGistModel;
import com.fastaccess.data.dao.FilesListModel;
import com.fastaccess.data.dao.ImgurReponseModel;
import com.fastaccess.data.dao.model.Gist;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.FileHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.PrefHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.ImgurProvider;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.BaseActivity;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import net.grandcentrix.thirtyinch.TiPresenter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by JediB on 5/25/2017.
 */

public class BannerInfoActivity extends AppCompatActivity {


	private static int READ_REQUEST_CODE = 256;

	@BindView(R.id.imageChooser)
	Button imageChooser;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_banner_info);
		getWindow().setStatusBarColor(getResources().getColor(R.color.material_indigo_700));
		ButterKnife.bind(this);
	}

	@OnClick(R.id.imageChooser) void onChooseImage() {
		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
			showFileChooser();
		else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
			requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_REQUEST_CODE);
	}

	private void showFileChooser() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), BundleConstant.REQUEST_CODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == BundleConstant.REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				RequestBody image = RequestBody.create(MediaType.parse("image/*"), new File(FileHelper.getPath(this, data.getData())));
				ImgurProvider.getImgurService().postImage("", image);
				RxHelper.getObserver(ImgurProvider.getImgurService().postImage("", image)).subscribe(imgurReponseModel -> {
					if (imgurReponseModel.getData() != null) {
						ImgurReponseModel.ImgurImage imageResponse = imgurReponseModel.getData();

						Gist.getMyGists(Login.getUser().getLogin()).forEach(gists -> {
							for (Gist gist : gists) {
								if (gist.getDescription().equalsIgnoreCase("header.fst")) {
									RxHelper.getObserver(RestProvider.getGistService().deleteGist(gist.getGistId()))
											.subscribe();
								}
							}
						});

						CreateGistModel createGistModel = new CreateGistModel();
						createGistModel.setDescription(InputHelper.toString("header.fst"));
						createGistModel.setPublicGist(true);
						HashMap<String, FilesListModel> modelHashMap = new HashMap<>();
						FilesListModel file = new FilesListModel();
						file.setFilename("header.fst");
						file.setContent(imageResponse.getLink());
						modelHashMap.put("header.fst", file);
						createGistModel.setFiles(modelHashMap);
						RxHelper.getObserver(RestProvider.getGistService().createGist(createGistModel))
								.subscribe(gist -> Toasty.success(this, getString(R.string.success)));
					}
				});
			}

			finish();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == READ_REQUEST_CODE) {
			if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					showFileChooser();
				} else {
					Toasty.error(this, getString(R.string.permission_failed)).show();
				}
			}
		}
	}

}
