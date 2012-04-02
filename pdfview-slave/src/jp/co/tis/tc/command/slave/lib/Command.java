package jp.co.tis.tc.command.slave.lib;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;


public class Command implements Serializable {
	private static final long serialVersionUID = 1048669706905653762L;
	
	private String rawCommand;
	public String getRawCommand() {
		return rawCommand;
	}
	private String action;
	public String getAction() {
		return action;
	}
	private Map<String, String> params = null;
	public Map<String, String> getParams() {
		if (params == null) {
			throw new InvalidParameterException("Command params is null");
		}
		return params;
	}
	
	public Command(String rawCommand) {
		this.rawCommand = rawCommand;
		String[] token = rawCommand.trim().split(",");
		this.action = token[0];
		if (token.length > 1) {
			this.params = new HashMap<String, String>();
			for (int i = 1; i < token.length; i++) {
				String[] kv = token[i].split("=");
				if (kv.length != 2) throw new InvalidParameterException("Command params is invalid format");
				params.put(kv[0], kv[1]);
			}
		}
	}
}
