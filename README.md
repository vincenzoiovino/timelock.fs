# timelock.fs
timelock.fs is an extension for the timelock.zone service (https://www.timelock.zone) that enables to encrypt to the future any file and decrypts it using the Windows Explorer.

## Installation
### Install from source
Compile the source code to a Java runnable file named `timeloc.fs.jar` in the `bin` folder or in any folder: the only requirement is that the file `install.bat` stays in the same folder as the runnable Jar you created.
Then follow the same steps as for the installation without compilation.

### Install without compilation
You should download the file `timelock.fs.zip` and unzip it.  The so created folder `timelock.fs` contains a file `install.bat`.
Edit the file `install.bat` with any editor (e.g. Notepad) and change the line:
```bash
setx YOUR_JAVA "C:\Program Files\Java\jdk-16.0.1"
```
to:
```bash
echo setx YOUR_JAVA Path
```
where Path is a path to your Java Runtime Environment (JRE) installation.

Once you did it, you can just run install.bat, grant admin permissions (needed to install the Windows extensions) and you are done

## Usage

### Encrypt to the future
You can right click on any file, e.g. `MyFile.pdf`, in Windows Explorer, select `Show more options` (this may depend on your Windows version) and then click on `timelock.fs.encrypt`.
You will be prompted to choose a date in the future in the format DD/MM/YYYY and then you will just have to click on the `Encrypt` button. It will be created a file `MyFile.pdf.tlcs` that contains the encrypted version of the file `MyFile.pdf`. You can now delete the original file if you want to hide it.


### Decrypt
The encrypted file is protected until the day before the date you selected.
When the day you selected is reached you can right click on the file, select `Show more options` (this may depend on your Windows version) and then click on `timelock.fs.decrypt`. Your file will be decrypted and you will recover the file `MyFile.pdf`.
