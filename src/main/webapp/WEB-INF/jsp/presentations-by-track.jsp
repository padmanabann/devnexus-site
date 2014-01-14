<%@page import="com.devnexus.ting.core.model.PresentationType"%>
<%@ include file="/WEB-INF/jsp/includes/taglibs.jsp" %>

<head>
	<title><c:out value="${event.title}"/> - Presentations</title>
</head>

<div id="devnex" class="jumbotron">
	<div class="container">
		<div id="banner">
			<h1 id="gray"><c:out value="${event.title}"/></h1>

			<h1 id="white">Presentations</h1>
			<h3>Data + Integration, Java/JavaEE/Spring, HTML5 + JavaScript, Alternative Languages, Cloud, Agile + Tools, Mobile</h3>
		</div>
	</div>
</div>

<div class="container">
	<c:set var="trackName" value="nill"/>
	<c:forEach items="${presentationList.presentations}" var="presentation" varStatus="status">
		<c:choose>
			<c:when test="${empty presentation.track}">
				<c:set var="localTrackName" value="Track not Assigned"/>
				<c:set var="trackStyle" value="defaultTrackStyle"/>
			</c:when>
			<c:otherwise>
				<c:set var="localTrackName" value="${presentation.track.name}"/>
				<c:set var="trackStyle" value="${presentation.track.cssStyleName}"/>
			</c:otherwise>
		</c:choose>
		<c:if test="${trackName ne localTrackName}">
			<c:set var="trackName" value="${localTrackName}"/>
			<c:if test="${!status.first}">
					</div>
					</div>
			</c:if>
			<h1 class="${trackStyle}"><strong><c:out value="${trackName}"/></strong><br/></h1>
			<div id="h4wrap"><h4>Presentations</h4></div>
			<div id="speakers">
				<div class="row" id="<c:out value="trackContainer${presentation.track.id}" default="trackContainerna"/>">
		</c:if>
		<div id="id-${presentation.id}" class="col-md-4 presentation track${presentation.track.id}">
			<%@ include file="/WEB-INF/jsp/presentations-include.jsp" %>
		</div>
	</c:forEach>
	</div>
	</div>
</div>

<content tag='bottom'>
	<script type="text/javascript">
		$(document).ready(function() {

			<c:forEach items="${presentationList.trackIdsAsString}" var="trackId">

			var $container${trackId} = $('#trackContainer${trackId}');

			console.log($container${trackId});

			$container${trackId}.imagesLoaded(function () {
				$container${trackId}.masonry({
						itemSelector: '.track${trackId}',
						columnWidth: '.track${trackId}',
						isAnimated: true
				});
			});

			</c:forEach>

		});
	</script>
</content>