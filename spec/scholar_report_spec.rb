require 'spec_helper'

RSpec.describe 'Scholar Report', type: :feature, js: true do
  it "works" do
    visit '/development.html'
    expect(page).to have_content('Scholar Report')
  end

  it "inserts scholar's name" do
    visit '/development.html?uri=https://scholars.duke.edu/individual/per2845042'
    expect(page).to have_content('John French')
  end

  it "shows 'no data available' for art works" do
    visit '/development.html?uri=https://scholars.duke.edu/individual/per2845042'
    check 'include-artisticWorks'
    art_works_section = find("#artistic-works")
    expect(art_works_section).to have_content('No data available.')
    expect(art_works_section).not_to have_selector('h3')
  end
end
