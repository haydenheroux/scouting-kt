<#-- @ftlvariable name="season" type="io.github.haydenheroux.scouting.models.team.Season" -->
<#-- @ftlvariable name="team" type="io.github.haydenheroux.scouting.models.team.Team" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <@layout.team_header team=team />
    <@layout.season_section season=season />
</@layout.header>
