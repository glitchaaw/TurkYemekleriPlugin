# REDZ Türk Yemekleri — Paper 1.21.11

Bu paket iki parçadan oluşur:
- `plugin/`: Java kaynak kodu. Sunucuda çalışması için Gradle ile `.jar` olarak derlenmelidir.
- `resource-pack/`: 25 özel piksel-art yemek görseli ve 1.21.11 item model dosyaları.

## Komutlar
- `/yemek` — yemek menüsünü açar; bir yemeğe tıklayınca envantere ekler.
- `/yemek liste` — yemek ID'lerini gösterir.
- `/yemek ver <oyuncu> <id> [adet]` — OP/yetkili için yemek verir.

Örnek:
`/yemek ver Oyuncu cig_kofte 3`

## Derleme
Java 21 ve Gradle kurulu bir bilgisayarda `plugin` klasöründe:
`gradle build`

Oluşan JAR:
`plugin/build/libs/TurkYemekleri-1.0.0.jar`

JAR dosyasını sunucunun `plugins` klasörüne koyup sunucuyu yeniden başlat.

## Resource pack
`resource-pack` klasörünü ZIP yap. ZIP'in kökünde `pack.mcmeta` ve `assets` klasörü olmalı.
Minecraft Java 1.21.11 istemcilerine bu ZIP'i resource pack olarak ver. Oyuncuların özel ikonları görebilmesi için resource pack'i etkinleştirmeleri gerekir.

## Notlar
- Bu ilk sürüm, 25 yemeği özel item olarak ekler.
- Sağ tıklama ile yenir ve açlık/doygunluk verir.
- Görseller 32x32 piksel, Minecraft tarzı basit piksel ikonlarıdır; istenirse sonradan daha ayrıntılı çizilebilir.
- Yemeklerin yenmesi Paper'ın FOOD ve CONSUMABLE bileşenleriyle yapılır. Bu kaynak proje bu ortamda derlenip test edilmedi. Sunucuya atmadan önce yedek al ve test sunucusunda dene.
