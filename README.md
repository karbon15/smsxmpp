This project aims to unify communications between SMS over DID via Asterisk and the XMPP instant messaging.

Below is an example of the SIP configuration and Dialplans configuration for SMSXMPP.

======= sip.conf ==================
[general]
...
accept_outofcall_message=yes
outofcall_message_context=smsxmpp-fromsip


======= extensions.conf ==================
[smsxmpp-fromsip]
exten => _X,1,Set(MSG=${MESSAGE(body)})
exten => _X,n,Set(FROM=${CUT(MESSAGE(from),:,2)})
exten => _X,n,Set(FROM=${CUT(FROM,@,1)})
exten => _X,n,Set(TO=${CUT(MESSAGE(to),:,2)})
exten => _X,n,Set(TO=${CUT(TO,@,1)})
exten => _X,n,Set(UUID=-${RAND(1,20000)})
exten => _X,n,Set(FILE(/var/spool/asterisk/sms/${STRFTIME(${EPOCH}-${UUID},,%F-%T)},,,a)=Channel: Local/s@smsxmpp-recv|)
exten => _X,n,Set(FILE(/var/spool/asterisk/sms/${STRFTIME(${EPOCH}-${UUID},,%F-%T)},,,a)=Extension: s|)
exten => _X,n,Set(FILE(/var/spool/asterisk/sms/${STRFTIME(${EPOCH}-${UUID},,%F-%T)},,,a)=Priority: 1|)
exten => _X,n,Set(FILE(/var/spool/asterisk/sms/${STRFTIME(${EPOCH}-${UUID},,%F-%T)},,,a)=Context: smsxmpp-recv|)
exten => _X,n,Set(FILE(/var/spool/asterisk/sms/${STRFTIME(${EPOCH}-${UUID},,%F-%T)},,,a)=SetVar: sms_host=did2.voip.les.net|)
exten => _X,n,Set(FILE(/var/spool/asterisk/sms/${STRFTIME(${EPOCH}-${UUID},,%F-%T)},,,a)=SetVar: sms_to=${TO}|)
exten => _X,n,Set(FILE(/var/spool/asterisk/sms/${STRFTIME(${EPOCH}-${UUID},,%F-%T)},,,a)=SetVar: sms_from=${FROM}|)
exten => _X,n,Set(FILE(/var/spool/asterisk/sms/${STRFTIME(${EPOCH}-${UUID},,%F-%T)},,,a)=SetVar: sms_body=${BASE64_ENCODE(${MSG})})
exten => _X,n,Noop("You received and SMS to: ${TO} from: ${FROM} msg: ${MSG}");

[smsxmpp-fromqueue]
exten => s,1,Set(MESSAGE(body)=${BASE64_DECODE(${sms_body})})
exten => s,n,MessageSend(sip:${sms_to}@${sms_host},"${sms_from}" )
