package com.cside.ap.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

@Service
public class MorphemeAnalysisSevice {

	private static final String OPEN_API_URL = "https://aiopen.etri.re.kr:8443/WiseNLU";

	private static final String OPEN_API_KEY = "a4f2a359-1c95-4c1f-a493-ae0d19a1d29b";

	public String getMorpheme(String str) {
		return this.wordMorph2(str);
	}

	public String wordMorph2(String text) {
		// text="네 안녕하세요 홍길동 교숩니다";
		Map<String, Object> request = new HashMap<>();
		Map<String, String> argument = new HashMap<>();

		argument.put("analysis_code", "ner");
		argument.put("text", text);

		request.put("access_key", OPEN_API_KEY);
		request.put("argument", argument);

		// System.out.println(text.length()+" "+text);
		URL url;
		Integer responseCode = null;
		String responBodyJson = null;
		Map<String, Object> responeBody = null;

		try {
			url = new URL(OPEN_API_URL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);

			Gson gson = new Gson();
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.write(gson.toJson(request).getBytes("UTF-8"));
			wr.flush();
			wr.close();

			responseCode = con.getResponseCode();

			if (responseCode==200) {
				InputStream is = con.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				StringBuffer sb = new StringBuffer();

				String inputLine = "";
				while ((inputLine = br.readLine()) != null) {
					sb.append(inputLine);
				}
				responBodyJson = sb.toString();

				// http 요청 오류 시 처리
				if (responseCode != 200) {
					// 오류 내용 출력
					System.out.println("[error] " + responBodyJson);
				}

				responeBody = gson.fromJson(responBodyJson, Map.class);
				Integer result = ((Double) responeBody.get("result")).intValue();
				Map<String, Object> returnObject;
				List<Map> sentences;

				// 분석 결과 활용
				returnObject = (Map<String, Object>) responeBody.get("return_object");
				sentences = (List<Map>) returnObject.get("sentence");

				Map<String, Morpheme> morphemesMap = new HashMap<String, Morpheme>();
				Map<String, NameEntity> nameEntitiesMap = new HashMap<String, NameEntity>();
				List<Morpheme> morphemes = null;
				List<NameEntity> nameEntities = null;

				for (Map<String, Object> sentence : sentences) {
					// 형태소 분석기 결과 수집 및 정렬
					List<Map<String, Object>> morphologicalAnalysisResult = (List<Map<String, Object>>) sentence
							.get("morp");
					for (Map<String, Object> morphemeInfo : morphologicalAnalysisResult) {
						String lemma = (String) morphemeInfo.get("lemma");
						Morpheme morpheme = morphemesMap.get(lemma);
						if (morpheme == null) {
							morpheme = new Morpheme(lemma, (String) morphemeInfo.get("type"), 1);
							morphemesMap.put(lemma, morpheme);
						} else {
							morpheme.count = morpheme.count + 1;
						}
					}

					// 개체명 분석 결과 수집 및 정렬
					List<Map<String, Object>> nameEntityRecognitionResult = (List<Map<String, Object>>) sentence
							.get("NE");
					for (Map<String, Object> nameEntityInfo : nameEntityRecognitionResult) {
						String name = (String) nameEntityInfo.get("text");
						NameEntity nameEntity = nameEntitiesMap.get(name);
						if (nameEntity == null) {
							nameEntity = new NameEntity(name, (String) nameEntityInfo.get("type"), 1);
							nameEntitiesMap.put(name, nameEntity);
						} else {
							nameEntity.count = nameEntity.count + 1;
						}
					}
				}
				List<Map<String, Object>> returnList = new ArrayList<>();
				Map<String, Object> tMap = new HashMap<>();
				if (0 < morphemesMap.size()) {
					morphemes = new ArrayList<Morpheme>(morphemesMap.values());
					morphemes.sort((morpheme1, morpheme2) -> {
						return morpheme2.count - morpheme1.count;
					});

					// 형태소들 중 명사들에 대해서 많이 노출된 순으로 출력 ( 최대 5개 )
					morphemes.stream().filter(morpheme -> {
						return morpheme.type.equals("NNG") || morpheme.type.equals("NNP")
								|| morpheme.type.equals("NNB");
					}).limit(20).forEach(morpheme -> {
						// System.out.println("[명사] " + morpheme.text + " (" + morpheme.count + ")");
						tMap.put(morpheme.text, morpheme.count);
					});
				}

				if (0 < nameEntitiesMap.size()) {
					nameEntities = new ArrayList<NameEntity>(nameEntitiesMap.values());
					nameEntities.sort((nameEntity1, nameEntity2) -> {
						return nameEntity2.count - nameEntity1.count;
					});

					// 인식된 개채명들 많이 노출된 순으로 출력 ( 최대 5개 )
					nameEntities.stream().limit(20).forEach(nameEntity -> {
						// System.out.println("[개체명] " + nameEntity.text + " (" + nameEntity.count +
						// ")");
						if (tMap.containsKey(nameEntity.text)) {
							tMap.put(nameEntity.text, (Integer) tMap.get(nameEntity.text) + nameEntity.count);
						} else {
							tMap.put(nameEntity.text, nameEntity.count);
						}
					});
				}

				for (String str : tMap.keySet()) {
					if (str.length() < 2 || (Integer) tMap.get(str) < 2) {
						continue;
					}
					// System.out.println(str+ tMap.get(str));
					Map<String, Object> returnMap = new HashMap<>();
					returnMap.put("word", str);
					returnMap.put("count", tMap.get(str));
					returnList.add(returnMap);
				}
				JSONArray jsonArray = getJsonArrayFromList(returnList);
				return jsonArray.toJSONString();
			} else {
				System.out.println("[error] Morpheme ");

			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";

	}

	public static JSONArray getJsonArrayFromList(List<Map<String, Object>> list) {
		JSONArray jsonArray = new JSONArray();
		for (Map<String, Object> map : list) {
			jsonArray.add(getJsonStringFromMap(map));
		}

		return jsonArray;
	}

	public static JSONObject getJsonStringFromMap(Map<String, Object> map) {
		JSONObject jsonObject = new JSONObject();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			jsonObject.put(key, value);
		}

		return jsonObject;
	}

	public static class Morpheme {
		final String text;
		final String type;
		Integer count;

		public Morpheme(String text, String type, Integer count) {
			this.text = text;
			this.type = type;
			this.count = count;
		}
	}

	public static class NameEntity {
		final String text;
		final String type;
		Integer count;

		public NameEntity(String text, String type, Integer count) {
			this.text = text;
			this.type = type;
			this.count = count;
		}
	}
}
