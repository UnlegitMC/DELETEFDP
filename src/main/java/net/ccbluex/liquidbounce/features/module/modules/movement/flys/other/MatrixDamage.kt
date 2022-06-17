package net.ccbluex.liquidbounce.features.module.modules.movement.flys.other

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.flys.FlyMode
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.value.*
import net.minecraft.network.play.server.S12PacketEntityVelocity
import kotlin.math.sin
import kotlin.math.cos


class MatrixDamage : FlyMode("MatrixDamage") {

    private val warn = BoolValue("${valuePrefix}DamageWarn",true)
    private val speedBoost = FloatValue("${valuePrefix}BoostSpeed", 1.15f, 0f, 3f)
    private val timer = FloatValue("${valuePrefix}Timer", 0.9f, 0f, 2f)
    private val boostTicks = IntegerValue("${valuePrefix}BoostTicks", 27,10,40)
    private val randomize = BoolValue("${valuePrefix}Randomize", true)
    private val randomAmount = IntegerValue("${valuePrefix}RandomAmount", 5, 0, 30).displayable {randomize.get()}

    private var velocitypacket = false
    private var packetymotion = 0.0
    private var tick = 0
    private var randomNum = 0.2

    override fun onEnable() {
        if (warn.get()) ClientUtils.displayChatMessage("§8§l[§c§lMatrixFly§8§l] §a§l U need make some damage to boost : bow , snowball , eggs...")
        velocitypacket = false
        packetymotion = 0.0
        tick = 0
    }

    override fun onUpdate(event: UpdateEvent) {
        if(velocitypacket) {
            if (randomize.get()) {
                randomNum = Math.random() * randomAmount.get() * 0.01
            } else {
                randomNum = 0.0
            }
            mc.timer.timerSpeed = timer.get()
            val yaw = Math.toRadians(mc.thePlayer.rotationYaw.toDouble())
            mc.thePlayer.motionX += (-sin(yaw) * (0.3 + (speedBoost.get().toDouble() / 10 ) + randomNum))
            mc.thePlayer.motionZ += (cos(yaw) * (0.3 + (speedBoost.get().toDouble() / 10 ) + randomNum))
            mc.thePlayer.motionY = packetymotion
            tick++
            if(tick>=boostTicks.get()) {
                mc.timer.timerSpeed = 1.0f
                velocitypacket = false
                packetymotion = 0.0
                tick = 0
            }

        }
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
    }

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is S12PacketEntityVelocity) {
            if (mc.thePlayer == null || (mc.theWorld?.getEntityByID(packet.entityID) ?: return) != mc.thePlayer) return
            if(packet.motionY / 8000.0 > 0.2) {
                velocitypacket = true
                packetymotion = packet.motionY / 8000.0
            }
        }
    }
}
