require 'spec_helper'

RSpec.describe 'Scholar Report', type: :feature, js: true do
  it "works" do
    visit '/development.html'
    expect(page).to have_content('Scholar Report')
  end

  context "for person with only appointments" do
    it "inserts scholar's name" do
      visit_report_for('9084042')
      expect(page).to have_content('Natalie Ammarell')
    end

    it "shows 'no data available' for art, pubs, grants" do
      visit_report_for('9084042')
      check 'include-artisticWorks'
      art_works_section = find("#artistic-works")
      expect(art_works_section).not_to have_selector('h3')
      expect(art_works_section.find('span').text).to eq('No data available.')

      check 'include-publications'
      pub_section = find("#publications")
      expect(pub_section).not_to have_selector('h3')
      expect(pub_section.find('span').text).to eq('No data available.')

      check 'include-grants'
      grant_section = find("#grants")
      expect(grant_section).not_to have_selector('h3')
      expect(grant_section.find('li').text).to eq('No data available.')
    end

    it "registers date filtering" do
      visit_report_for('9084042')
      find("input#start").click
      choose_date('Jan 15 2012')
      expect(page).to have_content('from 2012-01-15')

      find("input#end").click
      choose_date('Feb 22 2014')
      expect(page).to have_content('until 2014-02-22')
    end
  end

  def visit_report_for(id)
    visit "/development.html?uri=https://scholars.duke.edu/individual/per#{id}"
  end

  def choose_date(date)
    month, day, year = date.split(/\s+/)
    find('.datepicker-years .year', text: year).click
    find('.datepicker-months .month', text: month).click
    find('.datepicker-days .day', text: day).click
  end
end
