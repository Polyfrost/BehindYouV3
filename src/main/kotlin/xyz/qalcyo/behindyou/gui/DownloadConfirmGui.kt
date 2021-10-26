package xyz.qalcyo.behindyou.gui

import gg.essential.api.EssentialAPI
import gg.essential.api.gui.buildConfirmationModal
import gg.essential.api.utils.Multithreading
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.dsl.childOf
import net.minecraft.client.gui.GuiScreen
import xyz.qalcyo.behindyou.BehindYou
import xyz.qalcyo.behindyou.utils.Updater
import xyz.qalcyo.behindyou.utils.Updater.shouldUpdate
import java.io.File


class DownloadConfirmGui(private val parent: GuiScreen): WindowScreen(restoreCurrentGuiOnClose = true) {
    override fun initScreen(width: Int, height: Int) {
        super.initScreen(width, height)
        EssentialAPI.getEssentialComponentFactory().buildConfirmationModal {
            this.text = "Are you sure you want to update?"
            this.secondaryText = "(This will update from v${BehindYou.VERSION} to ${Updater.latestTag})"
            this.onConfirm = {
                EssentialAPI.getGuiUtil().openScreen(parent)
                Multithreading.runAsync {
                    if (Updater.download(
                            Updater.updateUrl,
                            File("mods/${BehindYou.NAME}-${Updater.latestTag.substringAfter("v")}.jar")
                        ) && Updater.download(
                            "https://github.com/Qalcyo/Deleter/releases/download/v1.2/Deleter-1.2.jar",
                            File(BehindYou.modDir.parentFile, "Deleter-1.2.jar")
                        )
                    ) {
                        EssentialAPI.getNotifications()
                            .push(BehindYou.NAME, "The ingame updater has successfully installed the newest version.")
                        Updater.addShutdownHook()
                        shouldUpdate = false
                    } else {
                        EssentialAPI.getNotifications().push(
                            BehindYou.NAME,
                            "The ingame updater has NOT installed the newest version as something went wrong."
                        )
                    }
                }
            }
            this.onDeny = {
                EssentialAPI.getGuiUtil().openScreen(parent)
            }
        } childOf this.window
    }
}