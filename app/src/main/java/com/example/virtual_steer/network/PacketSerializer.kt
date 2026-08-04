package com.example.virtual_steer.network

import com.example.virtual_steer.model.ControllerState
import java.nio.ByteBuffer
import java.nio.ByteOrder

class PacketSerializer {

    private var sequence: Short = 0

    fun serialize(state: ControllerState): ByteArray {

        val buffer = ByteBuffer
            .allocate(Protocol.PACKET_SIZE)
            .order(ByteOrder.LITTLE_ENDIAN)

        buffer.put(Protocol.HEADER)
        buffer.put(Protocol.VERSION)

        buffer.putShort(sequence++)
        buffer.putFloat(state.steering)
        buffer.putFloat(state.throttle)
        buffer.putFloat(state.brake)

        buffer.put(packButtons(state))

        buffer.put(0)

        buffer.putShort(0)

        buffer.putInt(0)

        val packet = buffer.array()

        val crc = CRC.calculate(packet)

        ByteBuffer
            .wrap(packet)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putShort(18, crc)

        return packet
    }

    private fun packButtons(
        state: ControllerState
    ): Byte {

        var buttons = 0

        if (state.handbrake)
            buttons = buttons or (1 shl 0)

        if (state.gearUp)
            buttons = buttons or (1 shl 1)

        if (state.gearDown)
            buttons = buttons or (1 shl 2)

        if (state.pause)
            buttons = buttons or (1 shl 3)

        if (state.horn)
            buttons = buttons or (1 shl 4)

        if (state.camera)
            buttons = buttons or (1 shl 5)

        return buttons.toByte()
    }
    fun deserialize(packet: ByteArray): ControllerState {

        require(packet.size == Protocol.PACKET_SIZE) {
            "Invalid packet size"
        }

        val buffer = ByteBuffer
            .wrap(packet)
            .order(ByteOrder.LITTLE_ENDIAN)

        // Verify header
        val header = buffer.get()
        require(header == Protocol.HEADER) {
            "Invalid packet header"
        }

        // Verify version
        val version = buffer.get()
        require(version == Protocol.VERSION) {
            "Unsupported protocol version"
        }

        // Read sequence number
        val sequence = buffer.short

        // Read analog values
        val steering = buffer.float
        val throttle = buffer.float
        val brake = buffer.float

        // Read buttons
        val buttons = buffer.get()

        // Skip reserved byte
        buffer.get()

        // Read CRC (verification can be added later)
        val crc = buffer.short

        // Skip reserved int
        buffer.int

        return ControllerState(
            steering = steering,
            throttle = throttle,
            brake = brake,

            handbrake = (buttons.toInt() and (1 shl 0)) != 0,
            gearUp = (buttons.toInt() and (1 shl 1)) != 0,
            gearDown = (buttons.toInt() and (1 shl 2)) != 0,
            pause = (buttons.toInt() and (1 shl 3)) != 0,
            horn = (buttons.toInt() and (1 shl 4)) != 0,
            camera = (buttons.toInt() and (1 shl 5)) != 0
        )
    }
}