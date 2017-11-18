package com.fastaccess.ui.modules.settings.sound

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import butterknife.BindView
import com.fastaccess.R
import com.fastaccess.data.dao.NotificationSoundModel
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.ui.base.BaseMvpBottomSheetDialogFragment
import com.fastaccess.ui.widgets.FontTextView

/**
 * Created by kosh on 23/07/2017.
 */
class NotificationSoundBottomSheet : BaseMvpBottomSheetDialogFragment<NotificationSoundMvp.View,
        NotificationSoundPresenter>(), NotificationSoundMvp.View {

    @BindView(R.id.title) lateinit var title: FontTextView
    @BindView(R.id.picker) lateinit var radioGroup: RadioGroup
    @BindView(R.id.ok) lateinit var okButton: Button

    private val padding: Int by lazy { resources.getDimensionPixelSize(R.dimen.spacing_xs_large) }
    private var canPlaySound: Boolean = false
    private val mediaPlayer = MediaPlayer()

    private var listener: NotificationSoundMvp.NotificationSoundListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = if (parentFragment is NotificationSoundMvp.NotificationSoundListener) {
            parentFragment as NotificationSoundMvp.NotificationSoundListener
        } else {
            context as NotificationSoundMvp.NotificationSoundListener
        }
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun fragmentLayout(): Int = R.layout.picker_dialog

    override fun providePresenter(): NotificationSoundPresenter = NotificationSoundPresenter()

    override fun onAddSound(sound: NotificationSoundModel) {
        val radioButtonView = RadioButton(context)
        val params = RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        radioButtonView.layoutParams = params
        radioButtonView.id = (radioGroup.childCount)
        radioButtonView.setPadding(padding, padding, padding, padding)
        radioButtonView.gravity = Gravity.CENTER_VERTICAL
        radioButtonView.tag = sound
        if (sound.isSelected) {
            radioButtonView.isChecked = true
        }
        radioButtonView.text = sound.name
        radioGroup.addView(radioButtonView)
    }

    override fun onCompleted() {
        canPlaySound = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title.text = getString(R.string.sound_chooser_title)
        okButton.visibility = View.VISIBLE
        presenter.loadSounds(arguments?.getString(BundleConstant.EXTRA))
        okButton.setOnClickListener {
            val selectedView: View? = radioGroup.getChildAt(radioGroup.checkedRadioButtonId)
            selectedView?.let {
                val sound = it.tag as NotificationSoundModel
                listener?.onSoundSelected(sound.uri)
            }
            dismiss()
        }
        radioGroup.setOnCheckedChangeListener { radioGroup, id ->
            if (!canPlaySound) return@setOnCheckedChangeListener
            val sound = radioGroup.getChildAt(id).tag as NotificationSoundModel
            if (mediaPlayer.isPlaying) mediaPlayer.stop()
            mediaPlayer.reset()
            mediaPlayer.setDataSource(context, sound.uri)
            mediaPlayer.prepare()
            mediaPlayer.start()
        }
    }

    override fun onDestroyView() {
        release()
        super.onDestroyView()
    }

    private fun release() {
        if (mediaPlayer.isPlaying) mediaPlayer.stop()
        mediaPlayer.release()
    }

    companion object {
        fun newInstance(default: String? = null): NotificationSoundBottomSheet {
            val fragment = NotificationSoundBottomSheet()
            fragment.arguments = Bundler.start().put(BundleConstant.EXTRA, default).end()
            return fragment
        }
    }
}