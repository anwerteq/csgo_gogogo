package com.chenerzhu.crawler.proxy.util.steamlogin;


public class DoLoginResultBean {

	private boolean success;

	private boolean requires_twofactor;

	private String message;

	private boolean clear_password_field;

	private boolean captcha_needed;

	private String captcha_gid;

	private boolean login_complete;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean isRequires_twofactor() {
		return requires_twofactor;
	}

	public void setRequires_twofactor(boolean requires_twofactor) {
		this.requires_twofactor = requires_twofactor;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isClear_password_field() {
		return clear_password_field;
	}

	public void setClear_password_field(boolean clear_password_field) {
		this.clear_password_field = clear_password_field;
	}

	public boolean isCaptcha_needed() {
		return captcha_needed;
	}

	public void setCaptcha_needed(boolean captcha_needed) {
		this.captcha_needed = captcha_needed;
	}

	public String getCaptcha_gid() {
		return captcha_gid;
	}

	public void setCaptcha_gid(String captcha_gid) {
		this.captcha_gid = captcha_gid;
	}

	public boolean isLogin_complete() {
		return login_complete;
	}

	public void setLogin_complete(boolean login_complete) {
		this.login_complete = login_complete;
	}

	class transfer_parameters {
		private String steamid;
		private String token;
		private String auth;
		private boolean remember_login;
		private String token_secure;

		public String getSteamid() {
			return steamid;
		}

		public void setSteamid(String steamid) {
			this.steamid = steamid;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String getAuth() {
			return auth;
		}

		public void setAuth(String auth) {
			this.auth = auth;
		}

		public boolean isRemember_login() {
			return remember_login;
		}

		public void setRemember_login(boolean remember_login) {
			this.remember_login = remember_login;
		}

		public String getToken_secure() {
			return token_secure;
		}

		public void setToken_secure(String token_secure) {
			this.token_secure = token_secure;
		}
	}
}
