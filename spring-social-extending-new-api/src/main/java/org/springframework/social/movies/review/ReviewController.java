/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.movies.review;

import java.security.Principal;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.social.movies.netflix.NetFlixApi;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/reviews")
public class ReviewController {
	
	private final ReviewRepository reviewRepository;

	private final NetFlixApi netflixApi;

	@Inject
	public ReviewController(NetFlixApi netflixApi, ReviewRepository reviewRepository) {
		this.netflixApi = netflixApi;
		this.reviewRepository = reviewRepository;
	}

	@RequestMapping(value="/new", method=RequestMethod.GET)
	public String searchForMovie() {
		return "review/movieSearch";
	}
	
	@RequestMapping(value="/new", method=RequestMethod.GET, params="searchTerm")
	public String selectMovie(Principal currentUser, String searchTerm, Model model) {
		model.addAttribute("titles", netflixApi.searchForTitles(searchTerm));
		return "review/movieSearch";
	}
	
	@RequestMapping(value="/new", method=RequestMethod.GET, params="title")
	public String reviewForm(String title, Model model) {
		String[] split = title.split("\\|");
		ReviewForm review = new ReviewForm();
		review.setMovieTitle(split[0]);
		review.setNetflixId(split[1]);
		model.addAttribute("review", review);
		return "review/form";
	}
	
	@RequestMapping(value="/new", method=RequestMethod.POST)
	public String submitReview(Principal currentUser, @Valid ReviewForm reviewForm) {
		reviewRepository.saveReview(reviewForm.newReview(currentUser.getName()));
		return "redirect:/";
	}
}
