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

    val title: FontTextView by lazy { view!!.findViewById<FontTextView>(R.id.title) }
    val radioGroup: RadioGroup by lazy { view!!.findViewById<RadioGroup>(R.id.picker) }
    val okButton: Button by lazy { view!!.findViewById<Button>(R.id.ok) }
    val padding: Int by lazy { resources.getDimensionPixelSize(R.dimen.spacing_xs_large) }
    var canPlaySound: Boolean = false
    val mediaPlayer = MediaPlayer()

    private var listener: NotificationSoundMvp.NotificationSoundListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (parentFragment is NotificationSoundMvp.NotificationSoundListener) {
            listener = parentFragment as NotificationSoundMvp.NotificationSoundListener
        } else {
            listener = context as NotificationSoundMvp.NotificationSoundListener
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

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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