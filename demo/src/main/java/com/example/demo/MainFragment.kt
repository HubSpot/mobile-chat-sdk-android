package com.example.demo

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demo.base.BaseFragment
import com.example.demo.base.Effect
import com.example.demo.base.observe
import com.example.demo.base.viewBinding
import com.example.demo.databinding.FragmentMainBinding
import com.example.demo.extention.navigateTo
import com.example.demo.util.DividerItemDecorator
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.messaging.FirebaseMessaging
import com.hubspot.mobilesdk.HubspotManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class MainFragment : BaseFragment(R.layout.fragment_main) {

    private val viewModel by viewModels<MainViewModel>()
    private val binding by viewBinding(FragmentMainBinding::bind)

    private var fcmToken: String = ""

    @Inject
    lateinit var hubspotManager: HubspotManager

    private var hasNotificationPermissionGranted = false

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            hasNotificationPermissionGranted = isGranted
            if (!isGranted) {
                viewModel.processAction(MainAction.UpdateNotificationPermissionProperty(false))
                if (Build.VERSION.SDK_INT >= 33) {
                    if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                        showNotificationPermissionRationale()
                    } else {
                        showSettingDialog()
                    }
                }
            } else {
                viewModel.processAction(MainAction.UpdateNotificationPermissionProperty(true))
                Toast.makeText(requireContext(), getString(R.string.notifications_permission_granted), Toast.LENGTH_SHORT).show()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObservers()
        val listItems = listOf(
            Content(getString(R.string.hubspot_button)),
            Content(getString(R.string.floating_button_examples)),
            Content(getString(R.string.nav_bars_toolbars))
        )
        with(binding.recyclerViewContent) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = MainAdapter(listItems, ::onMainItemClicked)
            addItemDecoration(
                DividerItemDecorator(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.divider
                    )
                )
            )
        }
        with(binding.toolbar) {
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_settings -> {
                        navigateTo(MainFragmentDirections.actionGoToSetting())
                        true
                    }

                    R.id.action_logout -> {
                        viewModel.processAction(MainAction.ClearData)
                        true
                    }

                    else -> false
                }
            }
        }
        binding.cardViewCustomProperty.setOnClickListener {
            navigateTo(MainFragmentDirections.actionGoToCustomProperty())
        }
        binding.cardViewCustomChatFlowId.setOnClickListener {
            navigateTo(MainFragmentDirections.actionGoToCustomChatFlow())
        }
        binding.buttonFCMToken.setOnClickListener {
            fetchFCMToken()
        }
        binding.buttonDeleteFCMToken.setOnClickListener {
            deleteFcmToken()
        }
        viewModel.processAction(MainAction.GetFCMToken)
        checkNotificationPermission()
        setUpUIForFCMToken()
    }

    private fun setUpUIForFCMToken() {
        if (fcmToken.isEmpty()) {
            with(binding) {
                textViewLabelFCM.isVisible = false
                textViewValueFCM.isVisible = false
                buttonFCMToken.isVisible = true
                buttonDeleteFCMToken.isVisible = false
            }
        } else {
            with(binding) {
                textViewLabelFCM.isVisible = true
                textViewValueFCM.isVisible = true
                buttonFCMToken.isVisible = false
                buttonDeleteFCMToken.isVisible = true
            }
        }
    }

    private fun fetchFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.e("Fetching FCM registration token failed" + task.exception)
                return@OnCompleteListener
            }
            fcmToken = task.result
            with(binding) {
                textViewLabelFCM.isVisible = true
                textViewValueFCM.isVisible = true
                textViewValueFCM.text = fcmToken
                buttonFCMToken.isVisible = false
                buttonDeleteFCMToken.isVisible = true
                Timber.i("FCM Token = $fcmToken")
                textViewValueFCM.setOnClickListener {
                    copyFCMText(textViewValueFCM.text.toString())
                }
            }
            viewModel.processAction(MainAction.SaveFCMToken(fcmToken))
            viewModel.processAction(MainAction.SetPushTokenInHubspotManager(fcmToken))
        })
    }

    private fun deleteFcmToken() {
        viewModel.processAction(MainAction.DeletePushTokenInHubspotManager(binding.textViewValueFCM.text.toString()))
    }

    private fun setUpObservers() {
        viewLifecycleOwner.observe(viewModel.effect, ::updateEffect)
    }

    override fun updateEffect(effect: Effect) {
        super.updateEffect(effect)
        when (effect) {
            is MainEffect.ShowFCMToken -> {
                if (!effect.fcmToken.isNullOrEmpty()) {
                    fcmToken = effect.fcmToken
                    with(binding) {
                        textViewLabelFCM.isVisible = true
                        textViewValueFCM.isVisible = true
                        buttonFCMToken.isVisible = false
                        textViewValueFCM.text = fcmToken
                        buttonDeleteFCMToken.isVisible = true
                        Timber.i("FCM Token = $fcmToken")
                        textViewValueFCM.setOnClickListener {
                            copyFCMText(textViewValueFCM.text.toString())
                        }
                    }
                }
            }

            is MainEffect.TokenDeleted -> {
                binding.buttonFCMToken.isVisible = true
                binding.textViewLabelFCM.isVisible = false
                binding.textViewValueFCM.isVisible = false
                binding.buttonDeleteFCMToken.isVisible = false
            }
        }
    }

    private fun copyFCMText(fcmToken: String) {
        val clipboard: ClipboardManager? =
            context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText("Copied Text", fcmToken)
        clipboard?.setPrimaryClip(clip)
    }

    private fun onMainItemClicked(content: Content) {
        when {
            content.data.contentEquals(getString(R.string.hubspot_button)) -> {
                navigateTo(MainFragmentDirections.actionGoToCustomButton())
            }

            content.data.contentEquals(getString(R.string.floating_button_examples)) -> {
                navigateTo(MainFragmentDirections.actionGoToFloatingButton())
            }

            else -> {
                navigateTo(MainFragmentDirections.actionGoToToolbar())
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            hasNotificationPermissionGranted = true
        }
    }

    private fun showSettingDialog() {
        MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.MaterialAlertDialog_Material3)
            .setTitle(getString(R.string.notification_permission_dialog_title))
            .setMessage(getString(R.string.notification_permission_dialog_message))
            .setPositiveButton(getString(R.string.notification_permission_dialog_ok)) { _, _ ->
                navigateToNotificationSettingsScreen()
            }
            .setNegativeButton(getString(R.string.notification_permission_dialog_cancel), null)
            .show()
    }

    private fun showNotificationPermissionRationale() {
        MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.MaterialAlertDialog_Material3)
            .setTitle(getString(R.string.notification_permission_rationale_dialog_title))
            .setMessage(getString(R.string.notification_permission_rationale_dialog_message))
            .setPositiveButton(getString(R.string.notification_permission_dialog_ok)) { _, _ ->
                if (Build.VERSION.SDK_INT >= 33) {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            .setNegativeButton(getString(R.string.notification_permission_dialog_cancel), null)
            .show()
    }

    private fun navigateToNotificationSettingsScreen() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
        startActivity(intent)
    }
}