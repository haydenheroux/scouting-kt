<#import "/common/enums.ftl" as enums />
<#-- TODO figure out why import match does not work -->
<#include "/common/match.ftl" />

<#macro team_link team>
    <a href="/teams/${team.number?c}">Team ${team.number?c} - ${team.name}</a>
</#macro>

<#macro team_number_link team_number>
    <a href="/teams/${team_number?c}">${team_number?c}</a>
</#macro>

<#macro team_number_year_link team_number year>
    <a href="/teams/${team_number?c}/${year?c}">Team ${team_number?c}</a>
</#macro>

<#macro team_year_link team year>
    <a href="/teams/${team.number?c}/${year?c}">Team ${team.number?c} - ${team.name}</a>
</#macro>

<#macro event_link event>
    <a href="/events/${event.code}">${event.name}</a>
</#macro>

<#macro match_link match event>
    <a href="/events/${event.code}/${match_to_serial(match)}">${match_to_text(match)}</a>
</#macro>

