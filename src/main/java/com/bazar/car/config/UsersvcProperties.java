package com.bazar.car.config;



import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "usersvc")
public class UsersvcProperties {

    private Otp otp = new Otp();
    private RateLimit rateLimit = new RateLimit();
    private Jwt jwt = new Jwt();
    private Channels channels = new Channels();
    private Sms sms = new Sms();

    public Otp getOtp() {
        return otp;
    }
    public void setOtp(Otp otp) {
        this.otp = otp;
    }

    public RateLimit getRateLimit() {
        return rateLimit;
    }
    public void setRateLimit(RateLimit rateLimit) {
        this.rateLimit = rateLimit;
    }
    public Jwt getJwt() {
        return jwt;
    }
    public void setJwt(Jwt jwt) {
        this.jwt = jwt;
    }
    public Channels getChannels() {
        return channels;
    }
    public void setChannels(Channels channels) {
        this.channels = channels;
    }
    public Sms getSms() {
        return sms;
    }
    public void setSms(Sms sms) {
        this.sms = sms;
    }

    public static class  Otp {
        private int digits;
        private int expiryMinutes;

        public int getDigits() {
            return digits;
        }
        public void setDigits(int digits) {
            this.digits = digits;
        }
        public int getExpiryMinutes() {
            return expiryMinutes;
        }
        public void setExpiryMinutes(int expiryMinutes) {
            this.expiryMinutes = expiryMinutes;
        }
    }

    public  static class  RateLimit {
        private int resendMinIntervalSeconds;
        private int maxSendsPerHour;
        public int getResendMinIntervalSeconds() {
            return resendMinIntervalSeconds;
        }
        public void setResendMinIntervalSeconds(int resendMinIntervalSeconds) {
            this.resendMinIntervalSeconds = resendMinIntervalSeconds;
        }
        public int getMaxSendsPerHour() {
            return maxSendsPerHour;
        }
        public void setMaxSendsPerHour(int maxSendsPerHour) {
            this.maxSendsPerHour = maxSendsPerHour;
        }
    }

    public static class Jwt {
        private String secret;
        private String issuer;
        private  int accessExpMinutes;

        public String getSecret() {
            return secret;
        }
        public void setSecret(String secret) {
            this.secret = secret;
        }
        public String getIssuer() {
            return issuer;
        }
        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }
        public int getAccessExpMinutes() {
            return accessExpMinutes;
        }
        public void setAccessExpMinutes(int accessExpMinutes) {
            this.accessExpMinutes = accessExpMinutes;
        }
    }

    public static class Channels {
        private String emailEnabled;
        private String smsEnabled;

        public String getEmailEnabled() {
            return emailEnabled;
        }

        public void setEmailEnabled(String emailEnabled) {
            this.emailEnabled = emailEnabled;
        }

        public String getSmsEnabled() {
            return smsEnabled;
        }

        public void setSmsEnabled(String smsEnabled) {
            this.smsEnabled = smsEnabled;
        }
    }
    public static class Sms {
        private String provider;
        private Msg91 msg91 = new Msg91();


        public String getProvider() {
            return provider;
        }
        public void setProvider(String provider) {
            this.provider = provider;
        }
        public Msg91 getMsg91() {
            return msg91;
        }
        public void setMsg91(Msg91 msg91) {
            this.msg91 = msg91;
        }

        public static  class Msg91 {
            private String authKey;
            private String flowId;
            private String senderId;
            private  String dltTeId;
            private boolean realTimeResponse;
            private int timeoutMs;

            public String getAuthKey() {
                return authKey;
            }
            public void setAuthKey(String authKey) {
                this.authKey = authKey;
            }
            public String getFlowId() {
                return flowId;
            }
            public void setFlowId(String flowId) {
                this.flowId = flowId;
            }
            public String getSenderId() {
                return senderId;
            }
            public void setSenderId(String senderId) {
                this.senderId = senderId;
            }
            public String getDltTeId() {
                return dltTeId;
            }
            public void setDltTeId(String dltTeId) {
                this.dltTeId = dltTeId;
            }
            public boolean isRealTimeResponse() {
                return realTimeResponse;
            }
            public void setRealTimeResponse(boolean realTimeResponse) {
                this.realTimeResponse = realTimeResponse;
            }
            public int getTimeoutMs() {
                return timeoutMs;
            }
            public void setTimeoutMs(int timeoutMs) {
                this.timeoutMs = timeoutMs;
            }
        }
    }



}
