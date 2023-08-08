<#-- @ftlvariable name="seasonReference" type="io.github.haydenheroux.scouting.models.team.SeasonReference" -->
<#import "_layout.ftl" as layout />
<@layout.header title="${seasonReference.year?c} - Team ${seasonReference.teamReference.number?c}">
    <@layout.team_header team=seasonReference.teamReference />
    <@layout.season_section season=seasonReference />
</@layout.header>
