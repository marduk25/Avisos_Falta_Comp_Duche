package com.dmsistemas.main;

import com.dmsistemas.conexion.Conexion;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class JavaAppAvisosCompPago extends Conexion {

    String RFC_E = "";
    String NOMBRE_E = "";
    String FACTURA = "";
    String UUID = "";
    String FOLIOWCXP = "";
    Date FECHA_PAGO;
    String CORREO = "";
    String REFERENCIA = "";
    String IMPORTE = "";

    public String getRFC_E() {
        return RFC_E;
    }

    public void setRFC_E(String RFC_E) {
        this.RFC_E = RFC_E;
    }

    public String getNOMBRE_E() {
        return NOMBRE_E;
    }

    public void setNOMBRE_E(String NOMBRE_E) {
        this.NOMBRE_E = NOMBRE_E;
    }

    public String getFACTURA() {
        return FACTURA;
    }

    public void setFACTURA(String FACTURA) {
        this.FACTURA = FACTURA;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getFOLIOWCXP() {
        return FOLIOWCXP;
    }

    public void setFOLIOWCXP(String FOLIOWCXP) {
        this.FOLIOWCXP = FOLIOWCXP;
    }

    public Date getFECHA_PAGO() {
        return FECHA_PAGO;
    }

    public void setFECHA_PAGO(Date FECHA_PAGO) {
        this.FECHA_PAGO = FECHA_PAGO;
    }

    public String getCORREO() {
        return CORREO;
    }

    public void setCORREO(String CORREO) {
        this.CORREO = CORREO;
    }

    public String getREFERENCIA() {
        return REFERENCIA;
    }

    public void setREFERENCIA(String REFERENCIA) {
        this.REFERENCIA = REFERENCIA;
    }

    public String getIMPORTE() {
        return IMPORTE;
    }

    public void setIMPORTE(String IMPORTE) {
        this.IMPORTE = IMPORTE;
    }

    public static void main(String[] args) throws MessagingException {
        JavaAppAvisosCompPago c = new JavaAppAvisosCompPago();
        c.Comprobante();
    }

    public void Comprobante() throws MessagingException {
        try {
            this.OpenPortal();
            Statement st = this.getCnportal().createStatement();
            ResultSet rs = st.executeQuery("SELECT RFC_E, NOMBRE_E, FACTURA, UUID, FOLIOWCXP, FECHA_PAGO, REFERENCIA, IMPORTE FROM FACTURA WHERE ESTATUS='PAGADA' AND ESTATUS_COM='NO EMITIDO'");
            if (!rs.isBeforeFirst()) {
                System.out.println(".......................................");
            } else {
                while (rs.next()) {
                    this.RFC_E = rs.getString("RFC_E");
                    this.NOMBRE_E = rs.getString("NOMBRE_E");
                    this.FACTURA = rs.getString("FACTURA");
                    this.UUID = rs.getString("UUID");
                    this.FOLIOWCXP = rs.getString("FOLIOWCXP");
                    this.FECHA_PAGO = rs.getDate("FECHA_PAGO");
                    this.REFERENCIA = rs.getString("REFERENCIA");
                    this.IMPORTE = rs.getString("IMPORTE");
                    Date nuevaFecha = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(this.FECHA_PAGO);
                    cal.add(Calendar.MONTH, 1);
                    nuevaFecha = cal.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                    System.out.println(sdf.format(nuevaFecha));
                    Date fecha = new Date();
                    System.out.println(sdf.format(fecha));
                    String f1 = nuevaFecha.toString().substring(0, 7);
                    String f2 = fecha.toString().substring(0, 7);
                    if (f1.equals(f2)) {
                        Statement st1 = this.getCnportal().createStatement();
                        ResultSet rs1 = st1.executeQuery("SELECT TOP(1) CORREO FROM USUARIO WHERE RFC='" + this.RFC_E + "'");
                        if (!rs1.isBeforeFirst()) {
                            System.out.println("______________________________________");
                        } else {
                            while (rs1.next()) {
                                this.CORREO = rs1.getString("CORREO");
                                System.out.println(this.CORREO);
                                EnviarCorreo();
                            }
                        }
                    }

                }
            }
            this.ClosePortal();
        } catch (SQLException ex) {
            Logger.getLogger(JavaAppAvisosCompPago.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void EnviarCorreo() throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.alestraune.net.mx");
        props.setProperty("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtp.port", "587");
        props.setProperty("mail.smtp.user", "portalproveedores@duche.com");
        props.setProperty("mail.smtp.auth", "true");
        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(true);

        BodyPart texto = new MimeBodyPart();
        texto.setContent("<html><head><title></title></head>"
                + "<body>"
                + "<table width='878' height='315' border='0' bordercolor='#0000FF' bgcolor='#FFFFFF'>"
                + "<tr>"
                + "<td height='50' colspan='3' bordercolor='#FFFFFF'><br><br></td>"
                + "</tr>"
                + "<tr>"
                + "<td colspan='3' bordercolor='#FFFFFF'><p align='left' style='font-family:calibri; font-size:17px'><font color='#004177'>ESTIMADO PROVEEDOR:  </font><br>"
                + "<font color='#17202a'></font><i><font color='#004177'> " + this.NOMBRE_E + " | " + this.RFC_E + "</font></i><br><br>"
                + "<font color='#17202a'></font><i><font color='#17202a'><b>NUESTRO SISTEMA HA DETECTADO QUE EL SIGUIENTE DOCUMENTO,<br> NO CUENTA CON COMPROBANTE DE PAGO REGISTRADO EN NUESTRO PORTAL.:</b></font></i> <br>"
                + "<font color='#17202a'></font> <font color='#086A87'><i></i></font><br>"
                + "<font color='#17202a'>Factura/Folio fiscal:</font> <font color='#004177'><i> " + this.FACTURA + " | " + this.UUID + " </i></font><br>"
                + "<font color='#17202a'>Recepci&oacute;n:</font> <font color='#004177'><i> " + this.REFERENCIA + " </i></font><br>"
                + "<font color='#17202a'>Cuenta por pagar no:</font> <font color='#004177'><i>WCXP" + this.FOLIOWCXP + " </i></font><br>"
                + "<font color='#17202a'>Monto pagado:</font> <font color='#004177'><i> $" + this.IMPORTE + " </i></font><br>"
                + "<font color='#17202a'></font> <font color='#17202a'><i><b></b> </i></font><br>"
                + "<font color='#17202a'></font> <font color='#17202a'><b>NOTA: DEBER&Aacute; SUBIR AL PORTAL ESTOS CFDI UNA VEZ PAGADA LA FACTURA Y EN LOS"
                + "PRIMEROS 7 D&Iacute;AS DEL MES SIGUIENTE COMO PLAZO M&Aacute;XIMO DESPU&Eacute;S DE HABER RECIBIDO EL PAGO DE LA(S) FACTURA(S).</b></font><br><br>"
                + "<font color='#17202a'><p></p></font><br>"
                + "<font color='#004177'><b>PORTAL PROVEEDORES | </font><font color='#004177'>COLOIDALES DUCH&Eacute;, S.A. DE C.V.</b></font></td>"
                + "</tr>"
                + "<tr>"
                + "<td width='725' bordercolor='#FFFFFF'><p align='left' style='font-family:calibri; font-size:17px'><br><br>"
                + "<a href='http://ducheproveedores.dyndns.info:9088/proveedores/' target='_blank'><img src='cid:image' width='20%'/></a></td>"
                + "<td width='422' bordercolor='#FFFFFF'></td>"
                + "<td width='422' rowspan='2' bordercolor='#FFFFFF'></td>"
                + "</tr>"
                + "<tr>"
                + "<td colspan='2' bordercolor='#17202a'><br><br><p align='justify' style='font-family:calibri; font-size:16px'>"
                + "<font color='#004177'><br> Favor de no responder a este correo ya que es un aviso del sistema, "
                + "si tiene alguna duda favor de contactar al &aacute;rea de Atenci&oacute;n a proveedores:<br>cuentasporpagartoluca@duche.com<br> "
                + "cuentasporpagarmexico@duche.com<br> amendoza@duche.com<br> bcarrillo@duche.com<br></font></p></td>"
                + "</tr>"
                + "</table>"
                + "</body></html>", "text/html");

        MimeMultipart multiParte = new MimeMultipart();
        BodyPart imagen = new MimeBodyPart();
        DataSource fds = new FileDataSource("C:\\img\\duche.png");
//        DataSource fds = new FileDataSource("/home/dmsistemas/Escritorio/logo2.png");
        imagen.setDataHandler(new DataHandler(fds));
        imagen.setHeader("Content-ID", "<image>");

        multiParte.addBodyPart(texto);
        // multiParte.addBodyPart(adjunto);
        multiParte.addBodyPart(imagen);

        MimeMessage message = new MimeMessage(session);

// Se rellena el From
        message.setFrom(new InternetAddress("portalproveedores@duche.com"));

// Se rellenan los destinatarios
        //message.addRecipients(Message.RecipientType.TO, us.getCorreo());
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(this.CORREO));
        message.addRecipient(Message.RecipientType.CC, new InternetAddress("duche.proveedores@gmail.com"));

// Se rellena el subject
        message.setSubject("AVISO DE FALTA DE COMPROBANTE");

// Se mete el texto y la foto adjunta.
        message.setContent(multiParte);

        Transport t = session.getTransport("smtp");
        t.connect("portalproveedores@duche.com", "ML310gen11");
        t.sendMessage(message, message.getAllRecipients());
        t.close();
    }

}
