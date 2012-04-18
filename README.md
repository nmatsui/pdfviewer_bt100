pdfviewer for MOVERIO (BT-100)
==============================

[EPSON MOVERIO BT-100][BT-100]で利用されることを念頭に置いた、PDF表示デモアプリケーションです。

光学透過型HMDであるBT-100はAndroidOSを搭載しているため、任意のアプリケーションを動作させることができます。
ただしAndroid本体である"コントローラー"の操作性がイマイチなため、"タッチディスプレイを直感的に操作する"ことを前提としたAndroidアプリが使いづらいのが現状です。

そこでGoogleCodeにてソースコードが公開されている[APV PDF Viewer][APV]を流用し、「他のAndroidフォンからMOVERIOを操作する」デモアプリを作ってみました。

動作環境
--------

* Master（音声とタッチディスプレイで操作するAndroidフォン）
    * Android 2.2以上
* Slave
    * MOVERIO BT-100

上記２台を同一の無線LANルータに接続し、TCP 10009 ポートで通信できるようにしてください

License
-------
Copyright(C) 2012 Nobuyuki Matsui (nobuyuki.matsui@gmail.com)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

[BT-100]:http://www.epson.jp/products/moverio/
[APV]:http://code.google.com/p/apv/
