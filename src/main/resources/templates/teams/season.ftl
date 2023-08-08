<#-- @ftlvariable name="seasonReference" type="io.github.haydenheroux.scouting.models.team.SeasonReference" -->
<#import "/common/_layout.ftl" as layout />
<@layout.header title="${seasonReference.year?c} - Team ${seasonReference.teamReference.number?c}">
    <@layout.team_link team=seasonReference.teamReference size="large" />
    <@layout.season_section season=seasonReference />
</@layout.header>
