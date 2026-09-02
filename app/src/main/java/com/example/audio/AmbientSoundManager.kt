package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.sqrt

enum class AmbientSoundTrack(
    val id: String,
    val titleFa: String,
    val descriptionFa: String
) {
    RAIN_DROPS("rain_drops", "🌧️ صدای طبیعی باران و رگبار", "بارش پیوسته و طبیعی باران روی زمین، چکه قطرات و طنین ملایم رعد"),
    CALM_PIANO("calm_piano", "🎹 پیانو آرامش‌بخش", "ملودی‌های آرام، گرم و گوش‌نواز پیانو مناسب مطالعه و تمرکز"),
    PEACEFUL_FOREST("peaceful_forest", "🌲 صدای آرامش‌بخش جنگل", "صدای وزش نسیم در میان درختان، خش‌خش برگ‌ها و نوای پرندگان")
}

class AmbientSoundManager {
    private var isPlaying = false
    private var currentTrack = AmbientSoundTrack.RAIN_DROPS
    private var volume = 0.8f
    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    fun isAmbientPlaying(): Boolean = isPlaying
    fun getCurrentTrack(): AmbientSoundTrack = currentTrack
    fun getVolume(): Float = volume

    fun setVolume(newVolume: Float) {
        volume = newVolume.coerceIn(0f, 1f)
        try {
            audioTrack?.setVolume(volume)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toggleAmbient(onStateChanged: (Boolean) -> Unit) {
        if (isPlaying) {
            stop()
            onStateChanged(false)
        } else {
            start(currentTrack)
            onStateChanged(true)
        }
    }

    fun playTrack(track: AmbientSoundTrack, onStateChanged: (Boolean) -> Unit) {
        currentTrack = track
        if (isPlaying) {
            stop()
            start(track)
            onStateChanged(true)
        } else {
            start(track)
            onStateChanged(true)
        }
    }

    private fun start(track: AmbientSoundTrack) {
        if (isPlaying) return
        isPlaying = true

        val sampleRate = 44100
        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val bufferSize = maxOf(minBufferSize, 8192)

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.setVolume(volume)
        audioTrack?.play()

        playbackJob = scope.launch {
            val random = Random()
            val shortBuffer = ShortArray(bufferSize / 2)

            when (track) {
                AmbientSoundTrack.RAIN_DROPS -> {
                    // 🌧️ ULTRA-REALISTIC NATURAL RAIN & GENTLE THUNDER/DRIPS ENGINE
                    // Multi-band acoustic filtering for rich rainfall texture
                    var pinkB0 = 0.0
                    var pinkB1 = 0.0
                    var pinkB2 = 0.0
                    var pinkB3 = 0.0
                    var pinkB4 = 0.0
                    var pinkB5 = 0.0
                    var brownLP = 0.0
                    var drizzleLP = 0.0
                    var splatterBP1 = 0.0
                    var splatterBP2 = 0.0

                    // Wave swell / wind dynamics
                    var swellPhase = 0.0

                    // Water droplet synthesizer
                    class DropVoice {
                        var active = false
                        var age = 0
                        var duration = 0
                        var startFreq = 1400.0
                        var endFreq = 650.0
                        var phase = 0.0
                        var amplitude = 0.12
                        var resonance = 0.8
                    }
                    val dropVoices = Array(8) { DropVoice() }

                    // Distant thunder generator
                    class ThunderVoice {
                        var active = false
                        var age = 0
                        var duration = 0
                        var rumbleLP = 0.0
                        var rumbleLP2 = 0.0
                        var rumblePhase = 0.0
                        var intensity = 0.0
                    }
                    val thunder = ThunderVoice()
                    var nextThunderCountdown = (sampleRate * (8 + random.nextDouble() * 12)).toInt()

                    while (isActive && isPlaying) {
                        for (i in shortBuffer.indices) {
                            val white = (random.nextGaussian() * 0.5)

                            // 1. High-fidelity Pink Noise (Paul Kellet 6-stage filter)
                            pinkB0 = 0.99886 * pinkB0 + white * 0.0555179
                            pinkB1 = 0.99332 * pinkB1 + white * 0.0750759
                            pinkB2 = 0.96900 * pinkB2 + white * 0.1538520
                            pinkB3 = 0.86650 * pinkB3 + white * 0.3104856
                            pinkB4 = 0.55000 * pinkB4 + white * 0.5329522
                            pinkB5 = -0.7616 * pinkB5 - white * 0.0168980
                            val pink = (pinkB0 + pinkB1 + pinkB2 + pinkB3 + pinkB4 + pinkB5 + white * 0.5362) * 0.065

                            // 2. Brown/Low rumble bed (deep body of rain)
                            brownLP = 0.96 * brownLP + white * 0.04
                            val brown = brownLP * 0.35

                            // 3. High-frequency drizzle texture
                            drizzleLP = 0.82 * drizzleLP + white * 0.18
                            val drizzle = (white - drizzleLP) * 0.04

                            // 4. Pavement splatter band-pass
                            splatterBP1 = 0.88 * splatterBP1 + (white - pink) * 0.12
                            splatterBP2 = 0.92 * splatterBP2 + splatterBP1 * 0.08
                            val splatter = (splatterBP1 - splatterBP2) * 0.12

                            // 5. Natural undulating rain swell
                            swellPhase += (2.0 * Math.PI * 0.05) / sampleRate
                            if (swellPhase > 2.0 * Math.PI) swellPhase -= 2.0 * Math.PI
                            val swell = 0.82 + 0.18 * sin(swellPhase) + 0.08 * sin(swellPhase * 2.3)

                            val rainBed = (pink * 0.45 + brown * 0.25 + drizzle * 0.15 + splatter * 0.25) * swell

                            // 6. Natural water droplet plops & spatters
                            if (random.nextDouble() < 0.0022) {
                                val freeDrop = dropVoices.firstOrNull { !it.active }
                                if (freeDrop != null) {
                                    freeDrop.active = true
                                    freeDrop.age = 0
                                    freeDrop.duration = (sampleRate * (0.035 + random.nextDouble() * 0.06)).toInt()
                                    val isHigh = random.nextDouble() < 0.4
                                    if (isHigh) {
                                        freeDrop.startFreq = 1200.0 + random.nextDouble() * 800.0
                                        freeDrop.endFreq = freeDrop.startFreq * (0.55 + random.nextDouble() * 0.25)
                                    } else {
                                        freeDrop.startFreq = 600.0 + random.nextDouble() * 500.0
                                        freeDrop.endFreq = freeDrop.startFreq * (0.65 + random.nextDouble() * 0.3)
                                    }
                                    freeDrop.phase = 0.0
                                    freeDrop.amplitude = 0.12 + random.nextDouble() * 0.18
                                    freeDrop.resonance = 0.85 + random.nextDouble() * 0.1
                                }
                            }

                            var dropsMix = 0.0
                            for (drop in dropVoices) {
                                if (drop.active) {
                                    val progress = drop.age.toDouble() / drop.duration
                                    val envelope = (1.0 - progress) * exp(-progress * 5.0)
                                    val curFreq = drop.startFreq + (drop.endFreq - drop.startFreq) * progress
                                    drop.phase += (2.0 * Math.PI * curFreq) / sampleRate
                                    if (drop.phase > 2.0 * Math.PI) drop.phase -= 2.0 * Math.PI
                                    dropsMix += sin(drop.phase) * envelope * drop.amplitude
                                    drop.age++
                                    if (drop.age >= drop.duration) {
                                        drop.active = false
                                    }
                                }
                            }

                            // 7. Distant ambient thunder / deep rumble
                            nextThunderCountdown--
                            if (nextThunderCountdown <= 0 && !thunder.active) {
                                thunder.active = true
                                thunder.age = 0
                                thunder.duration = (sampleRate * (4.0 + random.nextDouble() * 4.0)).toInt()
                                thunder.intensity = 0.12 + random.nextDouble() * 0.18
                                thunder.rumbleLP = 0.0
                                thunder.rumbleLP2 = 0.0
                                thunder.rumblePhase = 0.0
                                nextThunderCountdown = (sampleRate * (16.0 + random.nextDouble() * 22.0)).toInt()
                            }

                            var thunderMix = 0.0
                            if (thunder.active) {
                                val tProg = thunder.age.toDouble() / thunder.duration
                                // Smooth attack, undulating body, long smooth decay
                                val thunderEnv = sin(Math.PI * tProg) * exp(-tProg * 1.5)
                                thunder.rumbleLP = 0.982 * thunder.rumbleLP + (random.nextGaussian() * 0.5) * 0.018
                                thunder.rumbleLP2 = 0.975 * thunder.rumbleLP2 + thunder.rumbleLP * 0.025
                                thunder.rumblePhase += (2.0 * Math.PI * (48.0 + sin(tProg * Math.PI * 4.0) * 12.0)) / sampleRate
                                if (thunder.rumblePhase > 2.0 * Math.PI) thunder.rumblePhase -= 2.0 * Math.PI
                                
                                val subOsc = sin(thunder.rumblePhase) * 0.3
                                thunderMix = (thunder.rumbleLP2 * 2.2 + subOsc) * thunderEnv * thunder.intensity
                                thunder.age++
                                if (thunder.age >= thunder.duration) {
                                    thunder.active = false
                                }
                            }

                            val finalSample = (rainBed * 1.4 + dropsMix * 0.8 + thunderMix * 0.9).coerceIn(-1.0, 1.0)
                            shortBuffer[i] = (finalSample * 32767 * 0.72).toInt().toShort()
                        }
                        audioTrack?.write(shortBuffer, 0, shortBuffer.size)
                    }
                }

                AmbientSoundTrack.CALM_PIANO -> {
                    // 🎹 CALMING PEACEFUL PIANO (Warm chords & gentle melodic phrasing)
                    val chordNotes = arrayOf(
                        // C Major 7 (C3, G3, B3, E4, G4)
                        doubleArrayOf(130.81, 196.00, 246.94, 329.63, 392.00),
                        // A Minor 9 (A2, E3, G3, C4, E4)
                        doubleArrayOf(110.00, 164.81, 196.00, 261.63, 329.63),
                        // F Major 9 (F2, F3, A3, C4, E4)
                        doubleArrayOf(87.31, 174.61, 220.00, 261.63, 329.63),
                        // G Major (G2, D3, G3, B3, D4)
                        doubleArrayOf(98.00, 146.83, 196.00, 246.94, 293.66)
                    )

                    class PianoVoice {
                        var active = false
                        var freq = 220.0
                        var phase1 = 0.0
                        var phase2 = 0.0
                        var phase3 = 0.0
                        var phase4 = 0.0
                        var age = 0
                        var duration = (sampleRate * 4.5).toInt()
                        var velocity = 0.5
                    }

                    val voices = Array(8) { PianoVoice() }
                    var chordIndex = 0
                    var step = 0
                    var stepTimer = 0
                    val stepInterval = (sampleRate * 1.6).toInt()

                    while (isActive && isPlaying) {
                        for (i in shortBuffer.indices) {
                            if (stepTimer >= stepInterval) {
                                stepTimer = 0
                                val chord = chordNotes[chordIndex]
                                val noteFreq = chord[step % chord.size]

                                val v = voices.minByOrNull { if (it.active) it.age else 0 } ?: voices[0]
                                v.active = true
                                v.freq = noteFreq
                                v.age = 0
                                v.duration = (sampleRate * (3.8 + random.nextDouble() * 1.2)).toInt()
                                v.velocity = 0.35 + (if (step % 2 == 0) 0.15 else 0.05) + random.nextDouble() * 0.05
                                v.phase1 = 0.0
                                v.phase2 = 0.0
                                v.phase3 = 0.0
                                v.phase4 = 0.0

                                step++
                                if (step >= chord.size * 2) {
                                    step = 0
                                    chordIndex = (chordIndex + 1) % chordNotes.size
                                }
                            }

                            var pianoSample = 0.0
                            for (v in voices) {
                                if (v.active) {
                                    val t = v.age.toDouble() / sampleRate
                                    val attack = (1.0 - exp(-t * 80.0))
                                    val decayFundamental = exp(-t * 0.7)
                                    val decayH2 = exp(-t * 1.4)
                                    val decayH3 = exp(-t * 2.2)
                                    val decayH4 = exp(-t * 3.5)

                                    val s1 = sin(v.phase1) * decayFundamental
                                    val s2 = sin(v.phase2) * decayH2 * 0.35
                                    val s3 = sin(v.phase3) * decayH3 * 0.15
                                    val s4 = sin(v.phase4) * decayH4 * 0.06

                                    v.phase1 += (2.0 * Math.PI * v.freq) / sampleRate
                                    if (v.phase1 > 2.0 * Math.PI) v.phase1 -= 2.0 * Math.PI

                                    v.phase2 += (2.0 * Math.PI * v.freq * 2.0) / sampleRate
                                    if (v.phase2 > 2.0 * Math.PI) v.phase2 -= 2.0 * Math.PI

                                    v.phase3 += (2.0 * Math.PI * v.freq * 3.0) / sampleRate
                                    if (v.phase3 > 2.0 * Math.PI) v.phase3 -= 2.0 * Math.PI

                                    v.phase4 += (2.0 * Math.PI * v.freq * 4.0) / sampleRate
                                    if (v.phase4 > 2.0 * Math.PI) v.phase4 -= 2.0 * Math.PI

                                    pianoSample += (s1 + s2 + s3 + s4) * attack * v.velocity

                                    v.age++
                                    if (v.age >= v.duration) {
                                        v.active = false
                                    }
                                }
                            }

                            val out = (pianoSample * 0.45).coerceIn(-1.0, 1.0)
                            shortBuffer[i] = (out * 32767 * 0.65).toInt().toShort()
                            stepTimer++
                        }
                        audioTrack?.write(shortBuffer, 0, shortBuffer.size)
                    }
                }

                AmbientSoundTrack.PEACEFUL_FOREST -> {
                    // 🌲 RELAXING FOREST: Gentle wind in tree leaves, occasional sweet natural bird chirps & calm nature
                    var leafNoise1 = 0.0
                    var leafNoise2 = 0.0
                    var windPhase = 0.0

                    class BirdSong {
                        var active = false
                        var age = 0
                        var duration = 0
                        var baseFreq = 3200.0
                        var fmFreq = 24.0
                        var fmDepth = 400.0
                        var phase = 0.0
                        var songType = 0
                    }
                    val bird = BirdSong()
                    var nextBirdTimer = (sampleRate * 2.5).toInt()

                    while (isActive && isPlaying) {
                        for (i in shortBuffer.indices) {
                            windPhase += (2.0 * Math.PI * 0.08) / sampleRate
                            if (windPhase > 2.0 * Math.PI) windPhase -= 2.0 * Math.PI
                            val windStrength = 0.5 + 0.5 * sin(windPhase)

                            val white = (random.nextDouble() * 2.0 - 1.0)
                            leafNoise1 = 0.985 * leafNoise1 + white * 0.015
                            leafNoise2 = 0.995 * leafNoise2 + leafNoise1 * 0.04
                            val rustlingLeaves = leafNoise2 * (0.8 + 0.4 * windStrength) * 0.6

                            nextBirdTimer--
                            if (nextBirdTimer <= 0 && !bird.active) {
                                bird.active = true
                                bird.age = 0
                                bird.songType = random.nextInt(3)
                                bird.duration = when (bird.songType) {
                                    0 -> (sampleRate * 0.22).toInt()
                                    1 -> (sampleRate * 0.45).toInt()
                                    else -> (sampleRate * 0.35).toInt()
                                }
                                bird.baseFreq = 2600.0 + random.nextDouble() * 1200.0
                                bird.fmFreq = 18.0 + random.nextDouble() * 15.0
                                bird.fmDepth = 250.0 + random.nextDouble() * 300.0
                                bird.phase = 0.0
                                nextBirdTimer = (sampleRate * (3.0 + random.nextDouble() * 4.5)).toInt()
                            }

                            var birdSample = 0.0
                            if (bird.active) {
                                val t = bird.age.toDouble() / sampleRate
                                val progress = bird.age.toDouble() / bird.duration

                                val env = when (bird.songType) {
                                    0 -> sin(Math.PI * progress)
                                    1 -> sin(2.0 * Math.PI * progress).coerceAtLeast(0.0)
                                    else -> sin(Math.PI * progress) * (0.7 + 0.3 * sin(2.0 * Math.PI * bird.fmFreq * t))
                                }

                                val instantFreq = when (bird.songType) {
                                    0 -> bird.baseFreq + (progress * 600.0)
                                    1 -> bird.baseFreq + sin(4.0 * Math.PI * progress) * bird.fmDepth
                                    else -> bird.baseFreq + sin(2.0 * Math.PI * bird.fmFreq * t) * bird.fmDepth
                                }

                                bird.phase += (2.0 * Math.PI * instantFreq) / sampleRate
                                if (bird.phase > 2.0 * Math.PI) bird.phase -= 2.0 * Math.PI

                                birdSample = sin(bird.phase) * env * 0.18

                                bird.age++
                                if (bird.age >= bird.duration) {
                                    bird.active = false
                                }
                            }

                            val sample = (rustlingLeaves + birdSample).coerceIn(-1.0, 1.0)
                            shortBuffer[i] = (sample * 32767 * 0.6).toInt().toShort()
                        }
                        audioTrack?.write(shortBuffer, 0, shortBuffer.size)
                    }
                }
            }
        }
    }

    fun stop() {
        isPlaying = false
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        audioTrack = null
    }
}
