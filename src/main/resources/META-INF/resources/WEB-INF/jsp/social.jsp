<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jsp/includes/taglibs.jsp"%>
<% pageContext.setAttribute("lf", "\n"); %>

<title>${contextEvent.title} | Social</title>

<!-- intro -->
<section id="about" class="module parallax parallax-3">
	<div class="container header">
		<div class="row centered">
			<div class="col-md-10 col-md-offset-1">
				<div class="top-intro travel">
					<h4 class="section-white-title decorated"><span>Social</span></h4>
					<h5 class="intro-white-lead">Stay social my friends.</h5>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- /intro -->

<div class="masonry" style="margin-top: 1em;">
		<c:forEach items="${tweets}" var="tweet">
			<div class="col-md-3 tweet-box">
				<div class="panel panel-default">
					<div class="panel-heading" style="padding: 1px;"
					><img style="margin-right: 4px;" alt="${tweet.fromUser}" title="${tweet.fromUser}" src="${tweet.profileImageUrl}"/><a href="http://www.twitter.com/<c:out value="${tweet.fromUser}"/>"<c:out value="${tweet.fromUser}"/>"
					><c:out value="${tweet.fromUser}"/></a>
						<div class="btn-group pull-right"
						><a class="delete-tweet" href="#" class="btn btn-link btn-xs"
							><span class="glyphicon glyphicon-remove"></span></a>
							</div>
					</div>
					<div class="panel-body"><c:out value="${tweet.html}" escapeXml="false"/></div>
					<div class="panel-footer"><small><c:out value="${tweet.prettyTime}" /></small></div>
				</div>
			</div>
		</c:forEach>
</div>

<content tag='bottom'>

	<script src="${ctx}/assets/js/websocket/sockjs.js"></script>
	<script src="${ctx}/assets/js/websocket/stomp.js"></script>
	<script src="${ctx}/assets/js/other/handlebars-v1.1.2.js"></script>

	<script type="text/javascript">
		var startStopUrl = '<c:url value="/adapter/"/>';
		var latestTweetId = '0';
		var tweetsUrl    = '<c:url value="/tweets.json?sortOrder=DESCENDING"/>';
	</script>


	<script id="tweet-template" type="text/x-handlebars-template">
			{{#with tweet}}
			<div class="col-md-3 tweet-box">
				<div class="panel panel-default">
					<div class="panel-heading" style="padding: 1px;"
					><img style="margin-right: 4px;" alt="{{fromUser}}" title="{{fromUser}}" src="{{profileImageUrl}}"/><a href="http://www.twitter.com/{{fromUser}}"
					>{{fromUser}}</a>
					<div class="btn-group pull-right"
					><a class="delete-tweet" href="#" class="btn btn-link btn-xs"
						><span class="glyphicon glyphicon-remove"></span></a>
						</div>
					</div>

					<div class="panel-body">{{{html}}}</div>
					<div class="panel-footer"><small>{{prettyTime}}</small></div>
				</div>
			</div>
			{{/with}}
		</script>
	<script type="text/javascript">
		$(document).ready(function() {

			var $container = $('.masonry');

			$container.imagesLoaded(function () {
					$container.masonry({
							itemSelector: '.tweet-box',
							columnWidth: '.tweet-box',
							isAnimated: true
					});
			});

			Handlebars.registerHelper('link', function(url) {
				url = Handlebars.Utils.escapeExpression(url);
				console.log(url);
				return url;
			});

			var source = $("#tweet-template").html();
			tweetTemplate = Handlebars.compile(source);

					var socket = new SockJS('${ctx}/s/websocketbroker');
					var stompClient = Stomp.over(socket);
			stompClient.connect('', '', function(frame) {
				console.log('Connected ' + frame);

				stompClient.subscribe("/app/loadtweets", function(message) {
					console.log("Loading Tweets: " + message);
				});

				stompClient.subscribe("/queue/tweets", function(message) {
					var body = message.body;

					var tweetBody = jQuery.parseJSON(body);

					var context = {
						tweet : tweetBody
					};

					console.log(tweetBody);

					var html = tweetTemplate(context).trim();

					var tweets = $(".tweet-box");
					var h = $(html);
					$container.prepend(h);
					$container.imagesLoaded(function() {
						if (tweets.size() > 40) {
							$container.masonry('remove', tweets.slice(40 - tweets.size()));
						}
							$container.masonry('prepended', h);
					});

					// $container.prepend(html).masonry('reloadItems').masonry( 'resize' );

				});
				stompClient.subscribe("/queue/errors", function(message) {
					console.log(message);
				});
			}, function(error) {
				console.log("STOMP protocol error " + error);
			});

			$("body").on('click',".delete-tweet", function(event) {
				var t = $(this).closest(".tweet-box");

				$container.imagesLoaded(function() {
					$container.masonry('remove', t);
					$container.masonry('layout');
				});

				return false;
			});

		});
	</script>
</content>
