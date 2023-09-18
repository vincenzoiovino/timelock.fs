# timelock.fs
`timelock.fs` is an extension for the [timelock.zone](https://www.timelock.zone) service that enables to encrypt to the future and decrypts any file using Windows Explorer.
See also See also [tlcs-c](https://github.com/aragonzkresearch/tlcs-c/), [Timelock.zone.AndroidExample](https://github.com/vincenzoiovino/Timelock.zone.AndroidExample) and [TLCS Usage](https://github.com/aragonzkresearch/tlcs-c/blob/main/examples/howtoencrypt.md).
## Installation
### Install from source
Compile the source code to a Java runnable Jar file named `timeloc.fs.jar` in the `bin` folder or in any folder: the only requirement is that the file `install.bat` stays in the same folder as the runnable Jar you created.
Then follow the same steps as for the installation without compilation.

### Install without compilation
You should download the file `timelock.fs.zip` and unzip it.  The so created folder `timelock.fs` contains a file `install.bat`.
Edit the file `install.bat` with any editor (e.g. Notepad) and change the line:
```bash
setx YOUR_JAVA "C:\Program Files\Java\jdk-16.0.1"
```
to:
```bash
setx YOUR_JAVA Path
```
where Path is a path to your Java Runtime Environment (JRE) installation.
If you do not have JRE you can download it from here: https://www.oracle.com/java/technologies/downloads/

Once you did it, you can just run install.bat, grant admin permissions (needed to install the Windows extensions) and you are done.

## Usage

### Encrypt to the future
You can right click on any file, e.g. `MyFile.pdf`, in Windows Explorer, select `Show more options` (this may depend on your Windows version) and then click on `timelock.fs.encrypt`.
You will be prompted to choose a date in the future in the format DD/MM/YYYY and then you will just have to click on the `Encrypt` button. It will be created a file `MyFile.pdf.tlcs` that contains the encrypted version of the file `MyFile.pdf`. You can now delete the original file if you want to hide it.


### Decrypt
The encrypted file is protected until the day before the date you selected.
When the day you selected is reached you can right click on the file, select `Show more options` (this may depend on your Windows version) and then click on `timelock.fs.decrypt`. Your file will be decrypted and you will recover the file `MyFile.pdf`.

## Screenshots
<img src="screenshottlockfs1.jpg" width="30%" height="30%" />
<br>
<img src="screenshottlockfs2.jpg" width="30%" height="30%" />
<br>
<img src="screenshottlockfs3.jpg" width="30%" height="30%" />
<br>
<img src="screenshottlockfs4.jpg" width="30%" height="30%" />
