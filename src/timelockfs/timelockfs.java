package timelockfs;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.swing.*;


import org.zone.timelock.*;


class timelockfs {
	private static JLabel label;
	private static JLabel label2;
	private static JComboBox List;
	private static JTextField text;
	private static JPanel p;
	private static JFrame f;
	private static String file;
	private static final String scheme="secp256k1";
	private static final String TimelockZoneHeader="timelock.zone v10000001";
	private static final int length_date=10; // DDMMyyyyhh
	private static final String tlcs_extension=".tlcs";
	private static final String[] hourStrings = { "00", "01", "02", "03", "04", "05", "06", "07", "08","09", "10", "11", "12", "13","14","15","16","17","18","19","20","21","22","23" };

	private static int okcancel(String s) {
		int result = JOptionPane.showConfirmDialog((Component) null, s,
				"Alert", JOptionPane.OK_CANCEL_OPTION);
		return result;
	}

	private static String HourParsing(String hour) {
		if (Integer.valueOf(hour)<= 12) return hour+"AM";
		else return (Integer.valueOf(hour)-12)+"PM";
	}

	private static String toString(
			byte[] bytes,
			int length) {
		char[] chars = new char[length];

		for (int i = 0; i != chars.length; i++) {
			chars[i] = (char) (bytes[i] & 0xff);
		}

		return new String(chars);
	}

	private static String toString(
			byte[] bytes) {
		return toString(bytes, bytes.length);
	}


	private static void decrypt() {
		byte[] cipherText2 = null;
		Date strDate;


		try {

			KeyFactory kf = KeyFactory.getInstance("ECDH","BC");
			String s;
			Path tlcsfile=Paths.get(file);

			try {

				s=Files.readString(tlcsfile);

				if (s.length() <= length_date) return;

				try {
					cipherText2 = Base64.getDecoder().decode(s.substring(TimelockZoneHeader.length()+length_date));

				} catch (Exception e) {
					JOptionPane.showMessageDialog(f,
							"Invalid ciphertext format",
							"Error",
							JOptionPane.ERROR_MESSAGE);                    
					System.exit(1);
					return;
				}

				strDate = new Date();
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyhh");
					strDate = sdf.parse(s.substring(TimelockZoneHeader.length(), length_date+TimelockZoneHeader.length()));
					if (new Date().before(strDate)) {
						JOptionPane.showMessageDialog(f,
								"It is not time to decrypt yet. You must wait until " + s.substring(0, 2) + "/" + s.substring(2, 4) + "/" + s.substring(4, 8) + " (DD/MM/YYYY), " + HourParsing(s.substring(8,10))+  " to decrypt",
								"Warning",
								JOptionPane.WARNING_MESSAGE);                    
						System.exit(1);
						return;
					}


				} catch (ParseException e) {
					JOptionPane.showMessageDialog(f,
							"Invalid Date in the  ciphertext.",
							"Error",
							JOptionPane.ERROR_MESSAGE);  
					System.exit(1);
					return;
				}



			}

			catch (IOException e) {
				e.printStackTrace();

				JOptionPane.showMessageDialog(f,
						"Unable to read from file"+file+"\nThis can be due to the fact that you do not have permissions to read.",
						"Error",
						JOptionPane.ERROR_MESSAGE);        
				System.exit(1);
				return;
			}



			byte[] sk;

			long Round = Timelock.DateToRound(strDate);
			// retrieve SK from round R
			try {
				sk = Timelock.getSecretKeyFromRound(Round, scheme);
			} catch(Exception e) {
				JOptionPane.showMessageDialog(f,
						"Unable to retrive the secret key from the timelock.zone service. Try later.",
						"Error",
						JOptionPane.ERROR_MESSAGE);                       
				System.exit(1);
				return;
			}


			PrivateKey Sk = kf.generatePrivate(new PKCS8EncodedKeySpec(sk));

			Cipher iesCipher2 = Cipher.getInstance("ECIES","BC"); 
			// you can replace this with more secure instantiations of ECIES like "ECIESwithSHA256" etc. 
			// Notice that the bouncycastle Jar file we provide in the installation is old and may not support other ECIES modes. Replace it with a newer release.

			iesCipher2.init(Cipher.DECRYPT_MODE, Sk);
			// in newer  versions of BC you must use the following:
			// iesCipher2.init(Cipher.DECRYPT_MODE, Sk,new IESParameterSpec(null,null,128));
			// or other combinations based on your ECIESwithXX... algorithm

			byte[] plainText2 = new byte[iesCipher2.getOutputSize(cipherText2.length)];
			int ctlength2 = iesCipher2.update(cipherText2, 0, cipherText2.length - 1, plainText2);
			ctlength2 += iesCipher2.doFinal(plainText2, ctlength2);
			// System.out.println("decrypted plaintext: " + ctlength2 + " " + cipherText2.length + " " + toString(plainText2));

			Path decryptedfile=Paths.get(file.substring(0,file.length()-tlcs_extension.length())); 

			try {
				byte [] plainText3=new byte[plainText2.length]; 
				for (int i=0;i<plainText2.length;i++)plainText3[i]=plainText2[i];

				if (Files.exists(decryptedfile) && okcancel("The file "+decryptedfile.toString()+" already exists. Overwrite it?")==JOptionPane.CANCEL_OPTION) return;

				Files.write(decryptedfile,plainText3);

			}

			catch (IOException e) {
				e.printStackTrace();

				JOptionPane.showMessageDialog(f,
						"Unable to write to file"+file.substring(0,file.length()-tlcs_extension.length())+"\nThis can be due to the fact that the file is open or you do not have permissions to write.",
						"Error",
						JOptionPane.WARNING_MESSAGE);        
				System.exit(1);
			}




		} catch (InvalidKeySpecException | InvalidKeyException | NoSuchAlgorithmException |
				ShortBufferException | NoSuchPaddingException | BadPaddingException |
				IllegalBlockSizeException | NoSuchProviderException e) {
			// in newer versions of BC you must add InvalidAlgorithmParameterException
			e.printStackTrace();
			JOptionPane.showMessageDialog(f,
					"Unable to decrypt:\n" +e.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);         
			System.exit(1);
		}


	}



	private static void encrypt() {

		String s=text.getText();
		int index=List.getSelectedIndex();
		if (index==-1) {
			JOptionPane.showMessageDialog(f,
					"You should choose an hour.",
					"Warning",
					JOptionPane.WARNING_MESSAGE);    
			return;
		}
		s=s+"/"+hourStrings[index];
		System.out.println(s);
		Date strDate = new Date();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy/hh");
			strDate = sdf.parse(s);
			if (new Date().after(strDate)) {
				if (okcancel("You should select a date and hour in the future. Do you want to continue anyway with this date and hour?")==JOptionPane.CANCEL_OPTION) return;
			}


		} catch (ParseException e) {
			JOptionPane.showMessageDialog(f,
					"Invalid date format.",
					"Warning",
					JOptionPane.WARNING_MESSAGE);        
			return;
		}






		try {
			byte[] plainText = Files.readAllBytes(Paths.get(file));



			KeyFactory kf = KeyFactory.getInstance("ECDH","BC");

			Cipher iesCipher = Cipher.getInstance("ECIES", "BC");
			// you can replace this with more secure instantiations of ECIES like "ECIESwithSHA256" etc. 
			// Notice that the bouncycastle Jar file we provide in the installation is old and may not support other ECIES modes. Replace it with a newer release.
			long Round = Timelock.DateToRound(strDate);

			byte[] pk;
			try {
				pk = Timelock.getPublicKeyFromRound(Round, scheme);  // retrieve PK based on the given Round and scheme
			} catch(Exception e) {
				JOptionPane.showMessageDialog(f,
						"Timelock.zone service not working now. Try later.",
						"Error",
						JOptionPane.ERROR_MESSAGE);         
				return;
			}

			PublicKey pub = kf.generatePublic(new X509EncodedKeySpec(pk));


			iesCipher.init(Cipher.ENCRYPT_MODE, pub);
			// in newer  versions of BC you must use the following:
			//iesCipher.init(Cipher.ENCRYPT_MODE, pub, new IESParameterSpec(null,null,128));
			// or other combinations based on your ECIESwithXX... algorithm

			// set plaintext from file
			byte[] cipherText = new byte[iesCipher.getOutputSize(plainText.length)+1];


			int ctlength = iesCipher.update(plainText, 0, plainText.length, cipherText, 0);
			iesCipher.doFinal(cipherText, ctlength);
			cipherText[cipherText.length-1]='\0';
			String cipherTextBase64=Base64.getEncoder().encodeToString(cipherText);
			//  System.out.println(Base64.getEncoder().encodeToString(cipherText));

			String txtdate=s.substring(0,2)+s.substring(3,5)+s.substring(6,10)+s.substring(11,13); // convert DD/MM/yyyy/hh into DDMMyyyyhh

			Path tlcsfile=Paths.get(file+tlcs_extension);

			// copy to file.tlcs
			try {

				if (Files.exists(tlcsfile) && okcancel("The file "+tlcsfile.toString()+" already exists. Overwrite it?")==JOptionPane.CANCEL_OPTION) return;

				Files.write(tlcsfile, (TimelockZoneHeader+txtdate+cipherTextBase64).getBytes());


			}

			catch (IOException e) {
				e.printStackTrace();

				JOptionPane.showMessageDialog(f,
						"Unable to write to file"+file+".tlcs"+"\nThis can be due to the fact that the file is open or you do not have permissions to write.",
						"Error",
						JOptionPane.ERROR_MESSAGE);        
				return;
			}

		} catch (IOException | InvalidKeySpecException | InvalidKeyException | NoSuchAlgorithmException |
				ShortBufferException | NoSuchPaddingException | BadPaddingException |
				IllegalBlockSizeException | NoSuchProviderException e) {
			// in newer versions of BC you must add InvalidAlgorithmParameterException
			e.printStackTrace();
			JOptionPane.showMessageDialog(f,
					"Unable to encrypt:\n"+file+" "+e.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);         
			return; 
		}


		System.exit(0);


	}
	public static void main(String[] args) {

		Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);

		if (args.length<2) {
			JOptionPane.showMessageDialog(f,
					"timelock.fs can be used via the File Explorer.\nTo encrypt to the future: right click on any file, select \"timelock.fs.encrypt\" and follow the instructions.\nTo decrypt: right click on any file with .tlcs extension, select \"timelock.fs.decrypt\" and follow the instructions.\n\n"
							+ "©timelock.zone 2023.",
							"timelock.fs",
							JOptionPane.PLAIN_MESSAGE);
			return;	
		}
		else if (args[0].compareTo("encrypt")==0) {


			label = new JLabel("Choose a date (DD/MM/YYYY):");
			label2 = new JLabel("Choose a hour:");

			List = new JComboBox(hourStrings);

			text = new JTextField(20);
			JButton b = new JButton("Encrypt");
			p = new JPanel();
			p.add(label);
			p.add(text);
			p.add(label2);
			p.add(List);
			f = new JFrame();
			f.setTitle("Timelock.fs");
			f.getContentPane().add(p);
			p.add(b);
			f.pack();
			f.setVisible(true);
			file=args[1];
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					encrypt();
				}
			});


		} else {

			file=args[1];
			decrypt();

			JOptionPane.showMessageDialog(f,
					"Decrypted file written to: "+file.substring(0,file.length()-5),
					"Success",
					JOptionPane.INFORMATION_MESSAGE);

		}


	}




}

