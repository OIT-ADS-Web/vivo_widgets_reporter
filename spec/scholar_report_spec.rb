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
end
