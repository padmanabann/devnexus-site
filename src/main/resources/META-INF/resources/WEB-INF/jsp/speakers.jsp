<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="com.devnexus.ting.model.PresentationType"%>
<%@ include file="/WEB-INF/jsp/includes/taglibs.jsp" %>
<% pageContext.setAttribute("keynoteType", PresentationType.KEYNOTE); %>

<head>
	<title><c:out value="${event.title}"/> - Speakers</title>

	<style type="text/css">
		#speaker {
			opacity: 0.0;
		}
	</style>
</head>

	<!-- intro -->
	<section id="about" class="module parallax parallax-3">
		<div class="container header">
				<div class="row centered">
					<div class="col-md-10 col-md-offset-1">
						 <div class="top-intro travel">
							<h4 class="section-white-title decorated"><span>Speakers</span></h4>
							<h5 class="intro-white-lead">Discover how the industry's best minds use the latest technologies to build solutions.</h5>
							<br>
							<ul class="list-inline">
								<c:forEach items="${speakerList.speakers}" var="speaker" varStatus="status">
									<li><a class="speaker-link" href="#${speaker.firstName}_${speaker.lastName}"><c:out
										value="${speaker.firstName}"/> <c:out value="${speaker.lastName}"/></a></li>
								</c:forEach>
							</ul>
						</div>
					</div>
				</div>
		</div>
	</section>
	<!-- /intro -->

	<section id="speaker" class="bg-light-gray" style="margin-top: 0">
			<c:forEach items="${speakerList.speakers}" var="speaker" varStatus="status">
				<c:choose>
					<c:when test="${status.first && status.index%3 == 0}">
						<div class="row">
					</c:when>
					<c:when test="${not status.first && not status.last && status.index%3 == 0}">
						</div>
						<div class="row">
					</c:when>
				</c:choose>
				<div class="col-sm-4 masonryitem">
					<div id="${speaker.firstName}_${speaker.lastName}" class="speaker-member text-center">
						<c:if test="${speaker.picture != null}">
								<img src="${ctx}${baseSiteUrl}/speakers/${speaker.id}.jpg" class="img-responsive img-circle" alt="">
						</c:if>
						<h4 class="text-center"><c:out value="${speaker.firstName}"/> <c:out value="${speaker.lastName}"/></h4>
						<c:if test="${!empty speaker.presentations}">
							<ul>
							<c:forEach var="presentation" items="${speaker.presentations}" varStatus="loop">
								<li class="text-center">
									<a href="${siteUrl}/presentations#id-${presentation.id}"><c:out
										 value="${presentation.title}"/></a>
									<c:if test="${presentation.presentationType == keynoteType}">
										(Keynote)
									</c:if>
								</li>
							</c:forEach>
							</ul>
						</c:if>
						<p class="text-center">
							<c:if test="${!empty speaker.googlePlusId}">
								<a href="https://plus.google.com/<c:out value="${speaker.googlePlusId}" />" target="_blank">
									<button type="button" class="btn btn-default btn" data-toggle="tooltip" data-placement="bottom">
										<span class="fa fa-google"></span>
									</button>
								</a>
							</c:if>
							<c:if test="${!empty speaker.twitterId}">
								<a href="https://twitter.com/<c:out value="${speaker.twitterId}"/>" target="_blank">
									<button type="button" class="btn btn-default btn" data-toggle="tooltip" data-placement="bottom">
										<span class="fa fa-twitter"></span>
									</button>
								</a>
							</c:if>
							<c:if test="${!empty speaker.linkedInId}">
								<a href="https://www.linkedin.com/in/<c:out value="${speaker.linkedInId}" />" target="_blank">
									<button type="button" class="btn btn-default btn" data-toggle="tooltip" data-placement="bottom">
										<span class="fa fa-linkedin"></span>
									</button>
								</a>
							</c:if>
							<c:if test="${!empty speaker.githubId}">
								<a href="https://github.com/<c:out value="${speaker.githubId}" />" target="_blank">
									<button type="button" class="btn btn-default btn" data-toggle="tooltip" data-placement="bottom">
										<span class="fa fa-github"></span>
									</button>
								</a>
							</c:if>
							<c:if test="${!empty speaker.lanyrdId}">
								<a href="http://lanyrd.com/profile/<c:out value="${speaker.lanyrdId}" />/" target="_blank">
									<button type="button" class="btn btn-default btn" data-toggle="tooltip" data-placement="bottom">
										<span>L</span>
									</button>
								</a>
							</c:if>
						</p>
						<p><c:out value="${speaker.bioAsHtml}" escapeXml="false"/></p>
					</div>
				</div>
				<c:if test="${status.last}">
					</div>
				</c:if>
			</c:forEach>
		</div>
	</section>

	<jsp:include page="includes/questions.jsp"/>

<content tag='bottom'>
	<script type="text/javascript">

		$(document).ready(function() {


			if (location.hash) {
				setTimeout(function() {
					window.scrollTo(0, 0);
				}, 1);
			}

			var $container = $('#speaker');

			console.log($container);

			$container.imagesLoaded(function () {
				$container.masonry({
						itemSelector: '.masonryitem',
						columnWidth: '.masonryitem',
						isAnimated: true
				});

				var hash = window.location.hash;
				console.log('Hash is: ' + hash);
				if (!(hash === '')) {
					var el = $(hash);
					console.log('Scroll: ', el);
					$('html, body').animate({scrollTop: el.offset().top - 100}, 'fast');
				}

				$('#speaker').css('opacity', '1');
			});
		});
	</script>
</content>
