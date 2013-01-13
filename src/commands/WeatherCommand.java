/*
 * @author Raymond Hammarling
 * @description This command displays information either nearby the user, nearby the specified coordinates or nearby the specified user
 * @basecmd weather
 * @category misc
 */
package commands;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.geonames.GeoNamesException;
import org.geonames.WeatherObservation;
import org.geonames.WebService;
import org.geonames.utils.Distance;
import org.pircbotx.Channel;
import org.pircbotx.User;

import backend.Bot;

import commands.GeolocateCommand.IpInfoDb;

public class WeatherCommand extends Command {
	private static final Pattern coordsRegex = Pattern.compile("(-?\\d+(?:\\.\\d+)?) (-?\\d+(?:\\.\\d+)?)");
	
	@Override
	protected void initialize() {
		setName("Weather");
		setHelpText("Weather information for everyone!");
		addAlias("weather");
		addAlias("weather-nick");
	}

	@Override
	protected String format() {
		return super.format() + " {latitude} {longitude} | [nick]";
	}

	@Override
	public void execute(Bot bot, Channel chan, User user, String message) {
		if(message.startsWith(getCmdSequence())) message = message.substring(1);
		
		try {
			double latitude = 0, longitude = 0;
			String targetHost = user.getHostmask();
			
			String[] args = message.trim().split(" ", 2);
			
			if(args.length > 1) {
				if(args[0].equalsIgnoreCase("weather-nick")) {
					User targetUser = bot.getUser(args[1]);
					if(targetUser.getChannels().size() == 0) {
						passMessage(bot, chan, targetUser, "Sorry, I don't know about anyone called \"" + args[1] + "\"!");
						return;
					}
					else {
						targetHost = targetUser.getHostmask();
					}
				}
				else {
					Matcher matcher = coordsRegex.matcher(args[1]);
					if(matcher.find()) {
						latitude = Double.parseDouble(matcher.group(1));
						longitude = Double.parseDouble(matcher.group(2));
						targetHost = null;
					}
					else {
						invalidFormat(bot, chan, user);
						return;
					}
				}
			}
			
			if(targetHost != null) {
				IpInfoDb requester = GeolocateCommand.getIpInfoInstance();
				Map<String, String> result = requester.lookUp(InetAddress.getByName(targetHost));
				
				latitude = Double.parseDouble(result.get("latitude"));
				longitude = Double.parseDouble(result.get("longitude"));
			}
			
			WebService.setUserName("raykay");
			
			WeatherObservation obsrv = WebService.findNearByWeather(latitude, longitude);
			
			double dist = Distance.distanceKM(latitude, longitude, obsrv.getLatitude(), obsrv.getLongitude());
			String distStr = dist >= 1.0 ?
					String.format(Locale.ENGLISH, "%.1fkm", dist) :
					String.format(Locale.ENGLISH, "%.0fm", dist*1000);
			
			passMessage(bot, chan, user, String.format(Locale.ENGLISH,
					"Weather observation provided by %s (%s) at %s (%s ago), coords %.4f, %.4f (%s away): " +
					"%s, %s, humidity %.1f%%, temperature %.1f\u00B0C, dew point %.1f\u00B0C, wind speed %.2f m/s, " +
					"elevation %d m",
					obsrv.getStationName().trim(), obsrv.getCountryCode(),
					SimpleDateFormat.getInstance().format(obsrv.getObservationTime()), outputTime(new Date().getTime() - obsrv.getObservationTime().getTime()),
					obsrv.getLatitude(), obsrv.getLongitude(), distStr, obsrv.getWeatherCondition(),
					obsrv.getClouds(), obsrv.getHumidity(), obsrv.getTemperature(), obsrv.getDewPoint(),
					beaufortToMetersPerSecond(obsrv.getWindSpeed()), obsrv.getElevation()
					));
		}
		catch(GeoNamesException e) {
			passMessage(bot, chan, user, "There was a problem when looking up info: " + e.getMessage());
			e.printStackTrace();
		}
		catch(Exception e) {
			passMessage(bot, chan, user, "Oh my, an error: " +
					e.getClass().getSimpleName() + ": " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private double beaufortToMetersPerSecond(String beaufort) {
		return 0.836 * Math.pow(Double.parseDouble(beaufort), 3/2);
	}
	
	public String outputTime(long time) {
		time /= 1000;
		long seconds = time % 60;
		long minutes = (time % 3600) / 60;
		long hours = (time % 86400) / 3600;
		long days = (time % 31536000) / 86400;
		long years = time / 31536000;

		String output = "";
		if (years != 0)
			output += (output.length() > 0 ? ", " : "") + years + " year"
					+ (years > 1 ? "s" : "");
		if (days != 0)
			output += (output.length() > 0 ? ", " : "") + days + " day"
					+ (days > 1 ? "s" : "");
		if (hours != 0)
			output += (output.length() > 0 ? ", " : "") + hours + " hour"
					+ (hours > 1 ? "s" : "");
		if (minutes != 0)
			output += (output.length() > 0 ? ", " : "") + minutes + " minute"
					+ (minutes > 1 ? "s" : "");
		if (seconds != 0)
			output += (output.length() > 0 ? ", " : "") + seconds + " second"
					+ (seconds > 1 ? "s" : "");

		return output;
	}
}
