package com.universe.utils;

import java.util.Arrays;

public class Constants {

	public static final String[] CITIES;
	public static final String[] UNIVERSITIES;
	public static final String[] INTERESTS;

	static {
		CITIES = new String[] { "Toronto", "Ottawa", "Mississauga", "Brampton", "Hamilton", "London", "Markham",
				"Vaughan", "Kitchener", "Windsor", "Richmond Hill", "Oakville", "Burlington", "Sudbury", "Oshawa",
				"St. Catharines", "Barrie", "Cambridge", "Kingston", "Guelph", "Thunder Bay", "Waterloo", "Pickering",
				"Niagara Falls", "Whitby" };
		Arrays.sort(CITIES);

		UNIVERSITIES = new String[] { "University of Toronto", "York University", "McMaster University",
				"University of Waterloo", "Western University", "Queen's University", "University of Ottawa",
				"Carleton University", "University of Guelph", "Lakehead University", "Trent University",
				"Wilfrid Laurier University", "Brock University", "Ryerson University", "Laurentian University",
				"Nipissing University", "Ontario Tech University", "Algoma University" };
		Arrays.sort(UNIVERSITIES);

		INTERESTS = new String[] { "Sports", "Music", "Reading", "Travel", "Art", "Technology", "Cooking", "Fitness",
				"Gaming", "Movies", "Photography", "Fashion", "Environment", "Social Media", "Entrepreneurship",
				"Volunteering", "Writing", "Public Speaking", "Languages", "Hiking", "Yoga", "Meditation",
				"Health & Wellness", "Debate", "Community Service", "Cultural Activities", "Programming", "Robotics",
				"Startups", "Investing", "Astronomy", "Biology", "Physics", "Chemistry", "Mathematics", "Economics",
				"History", "Philosophy", "Political Science", "Psychology", "Sociology", "Graphic Design",
				"Video Editing", "Content Creation", "Podcasts", "Camping", "Food Tasting", "Dance", "Theater",
				"Stand-Up Comedy", "Board Games", "Card Games", "Puzzles", "Gardening", "Pets & Animals", "Anime",
				"Comics", "Creative Writing", "Journalism", "3D Modeling", "Virtual Reality", "Augmented Reality",
				"Cryptocurrency", "Blockchain", "Marketing", "Advertising", "Digital Art", "Painting", "Sculpting",
				"Music Production", "DJing", "Cars", "Motorcycles", "DIY Projects", "Home Decor", "Cooking Experiments",
				"Baking", "Mixology", "Event Planning", "Networking", "Career Development", "Fitness Challenges",
				"Weightlifting", "CrossFit", "Rugby", "Soccer", "Basketball", "Swimming", "Martial Arts",
				"Self-Defense", "Esports", "Streaming", "Interior Design", "Mindfulness", "Climate Activism",
				"Pet Care", "Charity Work", "Startup Pitches", "Business Plan Writing", "Urban Exploration",
				"Bird Watching", "Science Fiction", "Fantasy", "Classical Music", "Hip Hop", "Rock Music",
				"Electronic Music", "Jazz", "Blues", "Country Music", "Reggae", "K-Pop", "J-Pop", "Latino Music",
				"Dancehall", "Piano", "Guitar", "Drums", "Violin", "Networking Events", "TED Talks", "Personal Finance",
				"Home Brewing", "Cheese Tasting", "Public Relations", "Social Activism", "Podcast Hosting",
				"Speech Competitions", "Debate Club", "Yoga Retreats", "Survival Skills", "Outdoor Adventures",
				"Mountain Biking", "Skateboarding", "Snowboarding", "Skiing", "Fishing", "Kayaking", "Sailing",
				"Scuba Diving", "Freediving", "Surfing", "Archery", "Horseback Riding", "Cycling", "Triathlons",
				"Running", "Marathon Training", "Hunting", "Skydiving", "Bungee Jumping", "Parkour", "Geocaching",
				"Street Art", "Mural Painting", "Tattoo Art", "Hairstyling", "Cosplay", "Conventions",
				"LARPing (Live Action Role Playing)", "Escape Rooms", "Trivia Nights", "Improv Comedy", "Sketching",
				"Woodworking", "Metalworking", "Leather Crafting", "Knitting", "Crocheting", "Sewing", "Quilting",
				"Storytelling", "Magic Tricks", "Collecting", "Antique Hunting", "Vinyl Collecting", "Record Stores",
				"Museum Hopping", "Concerts", "Theater Plays", "Opera", "Ballet", "Trivia Games",
				"Learning New Languages", "Coding Hackathons", "Mathematics Competitions", "Model United Nations",
				"Science Fairs", "History Reenactment", "Cultural Festivals", "Travel Blogging", "Food Blogging",
				"Vlogging", "Fitness Blogging", "Book Clubs", "Study Groups", "Research Projects", "Academic Writing",
				"Grant Writing", "Academic Conferences", "Eco-Friendly Lifestyle", "Upcycling", "Minimalism",
				"Zero Waste", "Urban Gardening", "Sustainable Development", "Wildlife Conservation" };
		Arrays.sort(INTERESTS);
	}
}