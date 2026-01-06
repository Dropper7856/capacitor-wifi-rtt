# Wi-Fi RTT - Prérequis et Compatibilité

## Pourquoi j'ai "0 AP RTT Compatible" ?

### Raison principale : Les AP ne supportent pas 802.11mc

Le Wi-Fi RTT (Round Trip Time) nécessite que les **points d'accès (AP)** supportent le protocole **802.11mc**. 

**La plupart des routeurs Wi-Fi ne supportent PAS ce protocole**, même s'ils sont récents.

### Qu'est-ce que 802.11mc ?

802.11mc est une extension du standard Wi-Fi qui permet :
- La mesure précise du temps de trajet (Round Trip Time)
- Le positionnement en intérieur avec une précision de 1-2 mètres
- Le Fine Timing Measurement (FTM)

### Matériel requis

#### Côté appareil (smartphone/tablette)
✅ **Requis :**
- Android 9 (API 28) minimum
- Puce Wi-Fi supportant 802.11mc (la plupart des appareils récents)
- Permission de localisation accordée
- Wi-Fi activé

**Appareils compatibles (exemples) :**
- Google Pixel (2 et plus récent)
- Samsung Galaxy S9 et plus récent
- OnePlus 6 et plus récent
- Xiaomi Mi 8 et plus récent

#### Côté Point d'Accès (Router/AP)
✅ **Requis :**
- Support du protocole 802.11mc
- Firmware à jour
- Configuration FTM activée

**Points d'accès compatibles :**
- **Google Wi-Fi / Nest Wi-Fi** ✓
- **Aruba AP-5xx series** (ex: AP-515, AP-535) ✓
- **Cisco Catalyst 9100 series** ✓
- **Ruckus R750, R850** ✓
- **TP-Link EAP6xx series** (certains modèles) ✓

**Points d'accès NON compatibles :**
- La plupart des routeurs grand public (TP-Link Archer, Netgear, Asus, etc.)
- Les routeurs ISP fournis par défaut
- Les anciens points d'accès professionnels

## Comment tester si mes AP supportent RTT ?

### Méthode 1 : Via l'application
1. Lancez le scan Wi-Fi
2. Regardez les logs détaillés
3. Chaque AP affichera "RTT: ✓ OUI" ou "RTT: ✗ NON"

### Méthode 2 : Via ADB (pour développeurs)
```bash
adb shell dumpsys wifi
# Rechercher "80211mc" dans la sortie
```

### Méthode 3 : Documentation du fabricant
Consultez la fiche technique de votre AP/routeur et vérifiez :
- Support de **802.11mc**
- Support de **FTM (Fine Timing Measurement)**
- Support de **Wi-Fi RTT**

## Solutions pour tester le plugin

### Option 1 : Acheter un AP compatible (recommandé)
- **Google Nest Wi-Fi** (~100-150€) - Le plus simple
- **Aruba Instant On AP22** (~200€) - Pro abordable

### Option 2 : Utiliser un environnement de test
Certaines universités et entreprises disposent d'infrastructures Wi-Fi compatibles RTT.

### Option 3 : Émulation (limité)
⚠️ **Note :** Il n'existe pas de véritable émulation Android pour Wi-Fi RTT car cela nécessite du matériel physique réel.

## Vérifier la compatibilité de votre appareil

### Méthode programmatique (déjà implémentée)
```typescript
const support = await WifiRtt.isSupported();
console.log(support.supported); // true si l'appareil supporte RTT
console.log(support.reason); // Raison si non supporté
```

### Via les paramètres Android
1. Paramètres → À propos du téléphone
2. Version Android doit être ≥ 9.0
3. Chercher les spécifications Wi-Fi du modèle

## Statistiques d'adoption

📊 **Estimation du support RTT :**
- **Appareils Android (2024+)** : ~70% supportent RTT
- **Points d'accès grand public** : ~5% supportent 802.11mc
- **Points d'accès professionnels** : ~30% supportent 802.11mc
- **Infrastructure Google** : 100% compatible

## Questions fréquentes

### Q : Mon téléphone est compatible mais je trouve 0 AP RTT
**R :** C'est normal ! Vos routeurs Wi-Fi ne supportent probablement pas 802.11mc. Vous devez acheter des AP compatibles ou tester dans un environnement équipé.

### Q : Mon routeur est Wi-Fi 6 (802.11ax), ça suffit ?
**R :** Non. Wi-Fi 6 ≠ 802.11mc. Ce sont deux standards différents. Vous devez vérifier explicitement le support de 802.11mc.

### Q : Puis-je tester sur un émulateur ?
**R :** Non. Wi-Fi RTT nécessite du matériel physique réel (radio Wi-Fi + AP compatible).

### Q : Y a-t-il des alternatives à Wi-Fi RTT ?
**R :** Oui :
- **Bluetooth LE** + Trilatération (moins précis)
- **UWB (Ultra-Wideband)** (très précis mais nécessite tags UWB)
- **Beacons BLE** (précision room-level)
- **Computer Vision + SLAM**

## Ressources

- [Android RTT Documentation](https://developer.android.com/guide/topics/connectivity/wifi-rtt)
- [Wi-Fi Alliance - FTM](https://www.wi-fi.org/discover-wi-fi/wi-fi-location)
- [Google Wifi RTT Sample](https://github.com/android/connectivity-samples/tree/main/WifiRttScan)

## Conclusion

**Si vous voyez "0 AP RTT Compatible", c'est très probablement parce que vos points d'accès Wi-Fi ne supportent pas 802.11mc, PAS parce que le code est cassé.**

Pour vérifier que le plugin fonctionne, vous devez :
1. ✅ Avoir un appareil Android ≥ 9
2. ✅ Avoir le code déployé correctement
3. ✅ Avoir des AP compatibles 802.11mc → **C'est probablement ce qui manque**

