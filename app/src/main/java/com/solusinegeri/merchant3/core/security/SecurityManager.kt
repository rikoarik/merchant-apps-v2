package com.solusinegeri.merchant3.core.security

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Debug
import android.provider.Settings
import java.io.File
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

data class SecurityThreat(
    val type: ThreatType,
    val message: String,
    val severity: ThreatSeverity
)

enum class ThreatType {
    ROOT_DETECTED,
    MAGISK_DETECTED,
    FRIDA_DETECTED,
    EMULATOR_DETECTED,
    DEBUGGER_DETECTED,
    HOOK_DETECTED
}

enum class ThreatSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

class SecurityManager(private val context: Context) {
    
    private val knownRootApps = listOf(
        "com.noshufou.android.su",
        "com.noshufou.android.su.elite",
        "eu.chainfire.supersu",
        "com.koushikdutta.superuser",
        "com.thirdparty.superuser",
        "com.yellowes.su",
        "com.topjohnwu.magisk",
        "com.kingroot.kinguser",
        "com.kingo.root",
        "com.smedialink.oneclickroot",
        "com.zhiqupk.root.global",
        "com.alephzain.framaroot"
    )
    
    private val knownMagiskPaths = listOf(
        "/sbin/su",
        "/system/bin/su",
        "/system/xbin/su",
        "/data/local/xbin/su",
        "/data/local/bin/su",
        "/system/sd/xbin/su",
        "/system/bin/failsafe/su",
        "/data/local/su",
        "/su/bin/su",
        "/system/app/Superuser.apk",
        "/system/app/SuperSU.apk",
        "/system/app/SuperSU",
        "/system/app/Superuser",
        "/system/xbin/busybox",
        "/system/bin/busybox",
        "/data/local/busybox",
        "/data/local/bin/busybox",
        "/system/sd/xbin/busybox",
        "/system/bin/failsafe/busybox",
        "/data/local/busybox",
        "/su/bin/busybox"
    )
    
    private val knownFridaPorts = listOf(27042, 27043, 27044, 27045, 27046)
    
    fun performSecurityCheck(): List<SecurityThreat> {
        val threats = mutableListOf<SecurityThreat>()
        
        // Check for root
        threats.addAll(checkRootDetection())
        
        // Check for Magisk
        threats.addAll(checkMagiskDetection())
        
        // Check for Frida
        threats.addAll(checkFridaDetection())
        
//        // Check for emulator
//        threats.addAll(checkEmulatorDetection())
//
//        // Check for debugger
//        threats.addAll(checkDebuggerDetection())
        
        return threats
    }
    
    private fun checkRootDetection(): List<SecurityThreat> {
        val threats = mutableListOf<SecurityThreat>()
        
        // Check for su binary
        for (path in knownMagiskPaths) {
            if (File(path).exists()) {
                threats.add(SecurityThreat(
                    ThreatType.ROOT_DETECTED,
                    "Root binary detected at: $path",
                    ThreatSeverity.CRITICAL
                ))
            }
        }
        
        // Check for root apps
        val packageManager = context.packageManager
        for (packageName in knownRootApps) {
            try {
                packageManager.getPackageInfo(packageName, 0)
                threats.add(SecurityThreat(
                    ThreatType.ROOT_DETECTED,
                    "Root app detected: $packageName",
                    ThreatSeverity.HIGH
                ))
            } catch (e: PackageManager.NameNotFoundException) {
                // App not found, which is good
            }
        }
        
        // Check for root properties
        val buildTags = Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) {
            threats.add(SecurityThreat(
                ThreatType.ROOT_DETECTED,
                "Test keys detected in build tags",
                ThreatSeverity.MEDIUM
            ))
        }
        
        return threats
    }
    
    private fun checkMagiskDetection(): List<SecurityThreat> {
        val threats = mutableListOf<SecurityThreat>()
        
        // Check for Magisk specific paths
        val magiskPaths = listOf(
            "/sbin/.magisk",
            "/cache/.disable_magisk",
            "/data/cache/.disable_magisk",
            "/cache/magisk.log",
            "/data/cache/magisk.log",
            "/data/magisk.log",
            "/data/adb/magisk",
            "/data/adb/modules"
        )
        
        for (path in magiskPaths) {
            if (File(path).exists()) {
                threats.add(SecurityThreat(
                    ThreatType.MAGISK_DETECTED,
                    "Magisk detected at: $path",
                    ThreatSeverity.CRITICAL
                ))
            }
        }
        
        // Check for Magisk app
        try {
            context.packageManager.getPackageInfo("com.topjohnwu.magisk", 0)
            threats.add(SecurityThreat(
                ThreatType.MAGISK_DETECTED,
                "Magisk app detected",
                ThreatSeverity.CRITICAL
            ))
        } catch (e: PackageManager.NameNotFoundException) {
            // Magisk app not found, which is good
        }
        
        return threats
    }
    
    private fun checkFridaDetection(): List<SecurityThreat> {
        val threats = mutableListOf<SecurityThreat>()
        
        // Check for Frida server ports
        for (port in knownFridaPorts) {
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress("127.0.0.1", port), 1000)
                socket.close()
                threats.add(SecurityThreat(
                    ThreatType.FRIDA_DETECTED,
                    "Frida server detected on port: $port",
                    ThreatSeverity.HIGH
                ))
            } catch (e: IOException) {
                // Port not open, which is good
            }
        }
        
        // Check for Frida libraries
        val fridaLibs = listOf(
            "/data/local/tmp/frida-server",
            "/data/local/tmp/re.frida.server",
            "/system/lib/libfrida.so",
            "/system/lib64/libfrida.so"
        )
        
        for (lib in fridaLibs) {
            if (File(lib).exists()) {
                threats.add(SecurityThreat(
                    ThreatType.FRIDA_DETECTED,
                    "Frida library detected: $lib",
                    ThreatSeverity.HIGH
                ))
            }
        }
        
        return threats
    }
    
    private fun checkEmulatorDetection(): List<SecurityThreat> {
        val threats = mutableListOf<SecurityThreat>()
        
        // Check build properties
        val buildModel = Build.MODEL.lowercase()
        val buildManufacturer = Build.MANUFACTURER.lowercase()
        val buildProduct = Build.PRODUCT.lowercase()
        
        val emulatorIndicators = listOf(
            "sdk", "emulator", "simulator", "genymotion", "vbox", "virtualbox"
        )
        
        for (indicator in emulatorIndicators) {
            if (buildModel.contains(indicator) || 
                buildManufacturer.contains(indicator) || 
                buildProduct.contains(indicator)) {
                threats.add(SecurityThreat(
                    ThreatType.EMULATOR_DETECTED,
                    "Emulator detected: $indicator",
                    ThreatSeverity.MEDIUM
                ))
            }
        }
        
        // Check for emulator-specific files
        val emulatorFiles = listOf(
            "/system/lib/libc_malloc_debug_qemu.so",
            "/sys/qemu_trace",
            "/system/bin/qemu-props"
        )
        
        for (file in emulatorFiles) {
            if (File(file).exists()) {
                threats.add(SecurityThreat(
                    ThreatType.EMULATOR_DETECTED,
                    "Emulator file detected: $file",
                    ThreatSeverity.MEDIUM
                ))
            }
        }
        
        return threats
    }
    
    private fun checkDebuggerDetection(): List<SecurityThreat> {
        val threats = mutableListOf<SecurityThreat>()
        
        // Check if debugger is attached
        if (Debug.isDebuggerConnected()) {
            threats.add(SecurityThreat(
                ThreatType.DEBUGGER_DETECTED,
                "Debugger is connected",
                ThreatSeverity.HIGH
            ))
        }
        
        // Check for debugging flags
        val applicationInfo = context.applicationInfo
        if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            threats.add(SecurityThreat(
                ThreatType.DEBUGGER_DETECTED,
                "App is debuggable",
                ThreatSeverity.MEDIUM
            ))
        }
        
        // Check developer options
        val developerOptions = Settings.Global.getInt(
            context.contentResolver,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
            0
        )
        
        if (developerOptions == 1) {
            threats.add(SecurityThreat(
                ThreatType.DEBUGGER_DETECTED,
                "Developer options enabled",
                ThreatSeverity.LOW
            ))
        }
        
        return threats
    }
}
