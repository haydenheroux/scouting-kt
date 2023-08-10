<#-- @ftlvariable name="team" type="io.github.haydenheroux.scouting.models.team.Team" -->
<#-- @ftlvariable name="season" type="io.github.haydenheroux.scouting.models.team.Season" -->
<#import "/common/_layout.ftl" as layout />
<@layout.header title="${season.year?c} - Team ${team.number?c}">
    <h1><@layout.team_link team=team /></h1>
    <@layout.season_section team=team season=season />
</@layout.header>
