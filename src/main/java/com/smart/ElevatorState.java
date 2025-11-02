package com.smart;

/**
 * Asansörün mevcut durumunu temsil eden enum.
 * DOOR_CLOSING state'i kaldırıldı (kullanılmıyordu).
 */
public enum ElevatorState {
    /** Asansör boşta, hedef yok */
    IDLE,

    /** Yukarı doğru hareket ediyor */
    MOVING_UP,

    /** Aşağı doğru hareket ediyor */
    MOVING_DOWN,

    /** Kapı açık, yolcu indirme/bindirme yapılıyor */
    DOOR_OPEN,

    /** Bekleme durumu (opsiyonel, şu an kullanılmıyor) */
    WAITING,

    /** Acil durum, tüm operasyonlar durduruldu */
    EMERGENCY
}